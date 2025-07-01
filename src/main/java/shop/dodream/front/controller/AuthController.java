package shop.dodream.front.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import shop.dodream.front.client.AuthClient;
import shop.dodream.front.client.UserClient;
import shop.dodream.front.dto.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthClient authClient;
    private final UserClient userClient;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public String login(@ModelAttribute LoginRequest request, HttpServletResponse response, Model model, HttpServletRequest httpServletRequest) {
        try{
            ResponseEntity<Void> result = authClient.login(request);
            copyCookies(result, response);
            List<String> cookies = result.getHeaders().get(HttpHeaders.SET_COOKIE);
            String accessToken = cookies.get(0).split("=")[1];
            UserDto user = userClient.getUser(accessToken);
            HttpSession session = httpServletRequest.getSession();
            session.setAttribute("user", user);
            return "redirect:/home";
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
            copyCookies(result, response);
            return "redirect:/home";
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

    private void copyCookies(ResponseEntity<?> result, HttpServletResponse response) {
        List<String> cookies = result.getHeaders().get(HttpHeaders.SET_COOKIE);
        if (cookies != null) {
            for (String cookie : cookies) {
                response.addHeader(HttpHeaders.SET_COOKIE, cookie);
            }
        }
    }

    @PostMapping("/signup")
    public String signup(CreateAccountRequest request, UserAddressDto userAddressDto) {
        String password = request.getPassword();
        request.setPassword(passwordEncoder.encode(password));
        userClient.createUserAccount(new SignupRequest(request, userAddressDto));
        return "redirect:/home";
    }
}
