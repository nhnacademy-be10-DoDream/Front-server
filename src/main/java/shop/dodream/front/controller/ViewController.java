package shop.dodream.front.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/auth/login-form")
    public String loginForm() {
        return "auth/login";
    }

    @GetMapping("/auth/signup-form")
    public String signupForm() {
        return "auth/signup";
    }
}
