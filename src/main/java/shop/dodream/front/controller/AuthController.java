package shop.dodream.front.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import shop.dodream.front.dto.CreateAccountRequest;
import shop.dodream.front.dto.LoginRequest;
import shop.dodream.front.dto.UserAddressDto;
import shop.dodream.front.service.AuthService;

import java.io.IOException;
import java.util.Map;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public String login(@ModelAttribute LoginRequest request, Model model,HttpServletResponse response) throws IOException {
        try{
            authService.login(request,response);
            return "redirect:/";
        } catch (FeignException e) {
            return handleAuthException(e, request.getUserId(), model);
        }
    }

    @GetMapping("/payco/login")
    public void paycoLogin(HttpServletResponse response)throws IOException {
        String url = authService.getPaycoUrl();
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
            authService.paycoLogin(code, state, response);
            return "redirect:/";
        }catch (FeignException e) {
            return handleAuthException(e, null, model);
        }
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        authService.logout(request, response);
        return "redirect:/";

    }

    @PostMapping("/signup")
    public String signup(@Valid @ModelAttribute CreateAccountRequest request, @Valid @ModelAttribute UserAddressDto userAddressDto) {
        authService.signUp(request, userAddressDto);
        return "redirect:/";
    }

    private String handleAuthException(FeignException e, String fallbackUserId, Model model) {
        try {
            int status = e.status();

            if (status == HttpStatus.LOCKED.value()) { // 423
                ObjectMapper mapper = new ObjectMapper();
                Map<String, String> body = mapper.readValue(e.contentUTF8(), new TypeReference<>() {});

                if ("DORMANT".equalsIgnoreCase(body.get("status"))) {
                    String userId = body.getOrDefault("userId", fallbackUserId);
                    return "redirect:/auth/dormant/verify-form?userId=" + userId;
                }
            }

            if (status == HttpStatus.FORBIDDEN.value()) {
                model.addAttribute("error", "탈퇴한 계정입니다. 다른 계정으로 로그인 해주세요.");
                return "auth/login";
            }

        } catch (Exception ex) {
            model.addAttribute("error", "휴면 계정 처리 중 오류 발생");
            return "auth/login";
        }

        model.addAttribute("error", "로그인 실패: 아이디 또는 비밀번호가 올바르지 않습니다.");
        return "auth/login";
    }
}
