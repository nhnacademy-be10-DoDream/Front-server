package shop.dodream.front.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import shop.dodream.front.client.AuthClient;
import shop.dodream.front.client.UserClient;
import shop.dodream.front.dto.CreateAccountRequest;
import shop.dodream.front.dto.CreateAccountResponse;
import shop.dodream.front.dto.LoginRequest;
import shop.dodream.front.dto.UserAddressDto;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthClient authClient;
    private final UserClient userClient;
    @PostMapping("/login")
    public String login(LoginRequest request) {
        authClient.login(request);
        return "redirect:/home";
    }

    @PostMapping("/signup")
    public String signup(CreateAccountRequest request, UserAddressDto userAddressDto) {
        CreateAccountResponse response = userClient.createUserAccount(request);
        userClient.createUserAddress(response.getUserId(), userAddressDto);
        return "redirect:/home";
    }
}
