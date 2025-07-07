package shop.dodream.front.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import shop.dodream.front.client.AuthClient;
import shop.dodream.front.client.UserClient;
import shop.dodream.front.dto.*;
import shop.dodream.front.holder.AccessTokenHolder;
import shop.dodream.front.service.RedisUserSessionService;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthClient authClient;
    private final UserClient userClient;
    private final RedisUserSessionService redisUserSessionService;

    @PostMapping("/login")
    public String login(@ModelAttribute LoginRequest request, Model model, HttpServletResponse response) throws IOException {
        try{
            ResponseEntity<Void> result = authClient.login(request);
            HttpHeaders gatewayHeaders = result.getHeaders();
            if (gatewayHeaders.containsKey(HttpHeaders.SET_COOKIE)) {
                for (String cookieHeader : gatewayHeaders.get(HttpHeaders.SET_COOKIE)) {
                    response.addHeader(HttpHeaders.SET_COOKIE, cookieHeader);
                }
            }
            String accessToken = extractAccessTokenFromCookies(result.getHeaders().get(HttpHeaders.SET_COOKIE));
            AccessTokenHolder.set(accessToken);
            UserDto user = userClient.getUser();
            AccessTokenHolder.clear();
            redisUserSessionService.saveUser(accessToken,user);
            return "redirect:/";
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.LOCKED) {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    Map<String, String> body = mapper.readValue(
                            ex.getResponseBodyAsString(),
                            new TypeReference<>() {}
                    );
                    if ("DORMANT".equals(body.get("status"))) {
                        return "redirect:/dormant/verify-form?userId=" + request.getUserId();
                    }
                } catch (Exception e) {
                    model.addAttribute("error", "휴면 계정 처리 중 오류 발생");
                    return "auth/login";
                }
            } else if (ex.getStatusCode() == HttpStatus.FORBIDDEN) {
                model.addAttribute("error", "탈퇴한 계정입니다. 다른 계정으로 로그인 해주세요.");
                return "auth/login";
            }

            model.addAttribute("error", "로그인 실패: 아이디 또는 비밀번호가 올바르지 않습니다.");
            return "auth/login";
        }
    }


    @GetMapping("/payco/login")
    public void paycoLogin(HttpServletResponse response)throws IOException {
        String url = authClient.getAuthorizeUrl().getBody();
        response.sendRedirect(url);
    }

    @GetMapping("/payco/callback")
    public String paycoCallback(
            @RequestParam("code") String code,
            @RequestParam("state") String state,
            HttpServletResponse response,
            Model model
    ) {
        try {
            ResponseEntity<Void> result = authClient.paycoCallback(code, state);
            HttpHeaders gatewayHeaders = result.getHeaders();
            if (gatewayHeaders.containsKey(HttpHeaders.SET_COOKIE)) {
                for (String cookieHeader : gatewayHeaders.get(HttpHeaders.SET_COOKIE)) {
                    response.addHeader(HttpHeaders.SET_COOKIE, cookieHeader);
                }
            }
            String accessToken = extractAccessTokenFromCookies(result.getHeaders().get(HttpHeaders.SET_COOKIE));
            AccessTokenHolder.set(accessToken);
            UserDto user = userClient.getUser();
            AccessTokenHolder.clear();
            redisUserSessionService.saveUser(accessToken,user);
            return "redirect:/";
        }catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.LOCKED) {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    Map<String, String> body = mapper.readValue(
                            ex.getResponseBodyAsString(),
                            new TypeReference<>() {}
                    );
                    if ("DORMANT".equals(body.get("status"))) {
                        return "redirect:/dormant/verify-form?userId=" + body.get("userId");
                    }
                } catch (Exception e) {
                    model.addAttribute("error", "휴면 계정 처리 중 오류 발생");
                    return "auth/login";
                }
            } else if (ex.getStatusCode() == HttpStatus.FORBIDDEN) {
                model.addAttribute("error", "탈퇴한 계정입니다. 다른 계정으로 로그인해주세요.");
                return "auth/login";
            }

            model.addAttribute("error", "로그인 실패: 아이디 또는 비밀번호가 올바르지 않습니다.");
            return "auth/login";
        }
    }

    @PostMapping("/signup")
    public String signup(CreateAccountRequest request, UserAddressDto userAddressDto) {
//        String password = request.getPassword();
//        request.setPassword(passwordEncoder.encode(password));
        userClient.createUserAccount(new SignupRequest(request, userAddressDto));
        return "redirect:/";
    }

    private String extractAccessTokenFromCookies(List<String> cookies) {
        if (cookies == null) return null;
        for (String cookie : cookies) {
            if (cookie.startsWith("accessToken=")) {
                return cookie.split("=")[1].split(";")[0];
            }
        }
        return null;
    }
}