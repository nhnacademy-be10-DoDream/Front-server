package shop.dodream.front.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import shop.dodream.front.client.UserClient;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class UserController {
	private final UserClient userClient;

	@GetMapping(path = {"/profile", ""})
	public String getProfile(@CookieValue("accessToken") String accessToken,
							 Model model) {
		model.addAttribute("user", userClient.getUser(accessToken));
		model.addAttribute("activeMenu", "profile");
		return "mypage/profile";
	}

	@GetMapping("/addresses")
	public String getAddress(@CookieValue("accessToken") String accessToken,
							 Model model) {
		model.addAttribute("addresses", userClient.getAddresses(accessToken));
		model.addAttribute("activeMenu", "addresses");
		return "mypage/addresses";
	}


}