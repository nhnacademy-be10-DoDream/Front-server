package shop.dodream.front.interceptor;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import shop.dodream.front.client.AuthClient;
import shop.dodream.front.client.UserClient;
import shop.dodream.front.dto.TokenResponse;
import shop.dodream.front.dto.UserDto;
import shop.dodream.front.holder.AccessTokenHolder;
import shop.dodream.front.service.RedisUserSessionService;
import shop.dodream.front.util.CookieUtils;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RequestInterceptor implements HandlerInterceptor {

    private final AuthClient authClient;
    private final RedisUserSessionService redisUserSessionService;
    private final UserClient userClient;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String accessToken = CookieUtils.extractAccessCookie(request);
        String refreshToken = CookieUtils.extractRefreshCookie(request);

        if (accessToken == null && refreshToken != null) {
            ResponseEntity<TokenResponse> result = authClient.refresh(request.getHeader(HttpHeaders.COOKIE));
            TokenResponse tokenResponse = result.getBody();

            CookieUtils.setCookie(response, "accessToken", tokenResponse.getAccessToken(), tokenResponse.getExpiresIn(), true);
            accessToken = tokenResponse.getAccessToken();
            AccessTokenHolder.set(accessToken);
            try {
                UserDto user = userClient.getUser();
                redisUserSessionService.saveUser(accessToken, user);
            } finally {
                AccessTokenHolder.clear();
            }
        }

        if (accessToken != null) {
            AccessTokenHolder.set(accessToken);
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        AccessTokenHolder.clear();
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        if (modelAndView == null) return;

        String accessToken = AccessTokenHolder.get();
        if (accessToken == null) {
           accessToken = CookieUtils.extractAccessCookie(request);
        }

        if (accessToken != null) {
            UserDto user = redisUserSessionService.getUser(accessToken);
            if (user != null) {
                modelAndView.addObject("user", user);
            }
        }
    }

}
