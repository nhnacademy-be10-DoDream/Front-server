package shop.dodream.front.controller.auth;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
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
    public String login(@ModelAttribute LoginRequest request, Model model,HttpServletResponse response,RedirectAttributes redirectAttributes) throws IOException {
        try{
            authService.login(request,response);
            return "redirect:/";
        } catch (FeignException e) {
            return handleAuthException(e, request.getUserId(), model, redirectAttributes);
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
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            authService.paycoLogin(code, state, response);
            return "redirect:/";
        }catch (FeignException e) {
            return handleAuthException(e, null, model, redirectAttributes);
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

    private String handleAuthException(FeignException e, String fallbackUserId, Model model, RedirectAttributes redirectAttributes) {
        try {
            int status = e.status();
            ObjectMapper mapper = new ObjectMapper();

            Map<String, Object> body = null;
            String errorMessage = null;
            try {
                body = mapper.readValue(e.contentUTF8(), new TypeReference<>() {});
                errorMessage = (String) body.get("message");
            } catch (Exception ignore) {
            }

            if (status == HttpStatus.LOCKED.value() && body != null) {
                if ("DORMANT".equalsIgnoreCase((String)body.get("status"))) {
                    String userId = body.get("userId") != null ? String.valueOf(body.get("userId")) : fallbackUserId;
                    redirectAttributes.addFlashAttribute("userId",userId);
                    return "redirect:/auth/dormant/verify-form";
                }
            }
            if(errorMessage != null && !errorMessage.isBlank()) {
                model.addAttribute("error", errorMessage);
            }else {
                model.addAttribute("error", "로그인 실패: 아이디 또는 비밀번호가 올바르지 않습니다.");
            }
            return "auth/login";
        } catch (Exception ex) {
            model.addAttribute("error", "로그인 처리 중 오류가 발생했습니다.");
            return "auth/login";
        }
    }
}
