package shop.dodream.front.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import shop.dodream.front.client.UserClient;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserCheckController {
    private final UserClient userClient;

    @GetMapping("/check-email")
    public boolean checkEmailDuplicate(@RequestParam("email") String email) {
        return userClient.checkEmail(email);
    }

    @GetMapping("/check-user-id")
    public boolean checkUsernameDuplicate(@RequestParam("user-id") String userId) {
        return userClient.checkUserId(userId);
    }
}
