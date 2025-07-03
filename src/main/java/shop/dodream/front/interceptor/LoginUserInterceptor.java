package shop.dodream.front.interceptor;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import shop.dodream.front.dto.UserDto;
import shop.dodream.front.service.RedisUserSessionService;

@RequiredArgsConstructor
@Component
public class LoginUserInterceptor implements HandlerInterceptor {

    private final RedisUserSessionService redisUserSessionService;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView){
        if(modelAndView == null) {
            return;
        }

        String accessToken= null;
        if(request.getCookies() != null) {
            for(Cookie cookie : request.getCookies()) {
                if("accessToken".equals(cookie.getName())) {
                    accessToken = cookie.getValue();
                    break;
                }
            }
        }
        if(accessToken != null) {
            UserDto user = redisUserSessionService.getUser(accessToken);
            if(user != null) {
                modelAndView.addObject("user", user);
            }
        }
    }
}
