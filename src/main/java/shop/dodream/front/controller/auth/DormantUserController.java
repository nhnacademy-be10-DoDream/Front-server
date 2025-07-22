package shop.dodream.front.controller.auth;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import shop.dodream.front.client.AuthClient;

@Controller
@RequestMapping("/auth/dormant")
@RequiredArgsConstructor
public class DormantUserController {

    private final AuthClient authClient;

    @GetMapping("/verify-form")
    public String showVerifyForm(@ModelAttribute("userId") String userId, Model model){
        if(userId==null || userId.isEmpty()){
            model.addAttribute("error","인증 정보가 없습니다. 다시 로그인 해주세요.");
            return "auth/login";
        }
        model.addAttribute("userId", userId);
        return "auth/verify";
    }

    @PostMapping("/verify")
    public String verifyCode(@RequestParam String userId,
                             @RequestParam String code,
                             Model model) {
        try {
            ResponseEntity<String> response = authClient.verifyDormantCode(userId, code);
            model.addAttribute("verifySuccess", true);
            model.addAttribute("message", response.getBody());
            return "auth/verify";
        } catch (FeignException.Unauthorized e) {
            model.addAttribute("error", "인증번호가 일치하지 않습니다.");
            model.addAttribute("userId", userId);
            return "auth/verify";
        } catch (FeignException e) {
            model.addAttribute("error", "서버 오류가 발생했습니다.");
            model.addAttribute("userId", userId);
            return "auth/verify";
        }
    }

    @PostMapping("/send")
    @ResponseBody
    public ResponseEntity<Void> sendCode(@RequestParam String userId) {
        try {
            authClient.sendDormantCode(userId);
            return ResponseEntity.ok().build();
        } catch (FeignException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
