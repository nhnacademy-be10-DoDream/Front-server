package shop.dodream.front.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import shop.dodream.front.client.AuthClient;
import shop.dodream.front.client.UserClient;
import shop.dodream.front.dto.*;
import shop.dodream.front.holder.AccessTokenHolder;
import shop.dodream.front.util.CookieUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final AuthClient authClient;
    private final UserClient userClient;
    private final RedisUserSessionService redisUserSessionService;

    public void login(LoginRequest request, HttpServletResponse response) {
        TokenResponse tokenResponse = authClient.login(request).getBody();
        CookieUtils.setCookie(response, "accessToken", tokenResponse.getAccessToken(), tokenResponse.getExpiresIn(), true);
        CookieUtils.setCookie(response, "refreshToken", tokenResponse.getRefreshToken(), 86400, true);
        AccessTokenHolder.set(tokenResponse.getAccessToken());
        try {
            UserDto user = userClient.getUser();
            redisUserSessionService.saveUser(tokenResponse.getAccessToken(), user);
        } finally {
            AccessTokenHolder.clear();
        }



    }
    public String getPaycoUrl(){
        return authClient.getAuthorizeUrl().getBody();
    }

    public void paycoLogin(String code, String state, HttpServletResponse response) {
        TokenResponse tokenResponse = authClient.paycoCallback(code, state).getBody();
        CookieUtils.setCookie(response, "accessToken", tokenResponse.getAccessToken(), tokenResponse.getExpiresIn(), true);
        CookieUtils.setCookie(response, "refreshToken", tokenResponse.getRefreshToken(), 86400, true);
        AccessTokenHolder.set(tokenResponse.getAccessToken());
        try {
            UserDto user = userClient.getUser();
            redisUserSessionService.saveUser(tokenResponse.getAccessToken(), user);
        } finally {
            AccessTokenHolder.clear();
        }

    }

    public void logout(HttpServletRequest request,HttpServletResponse response) {
        String cookieHeader = request.getHeader(HttpHeaders.COOKIE);
        try {
            authClient.logout(cookieHeader);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        String accessToken = CookieUtils.extractAccessCookie(request);

        if (accessToken != null) {
            redisUserSessionService.deleteUser(accessToken);
        }

        CookieUtils.deleteCookie(response, "accessToken");
        CookieUtils.deleteCookie(response, "refreshToken");
    }

    public void signUp(CreateAccountRequest request, UserAddressDto userAddressDto){
        userClient.createUserAccount(new SignupRequest(request,userAddressDto));
    }

}
