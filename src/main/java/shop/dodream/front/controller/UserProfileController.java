package shop.dodream.front.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import shop.dodream.front.client.OrderClient;
import shop.dodream.front.client.UserClient;
import shop.dodream.front.dto.UserAddressDto;
import shop.dodream.front.dto.UserUpdateDto;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class UserProfileController {
	private final UserClient userClient;
	private final OrderClient orderClient;

	@GetMapping(path = {"/profile", ""})
	public String getProfile(@CookieValue("accessToken") String accessToken,
							 Model model) {
		model.addAttribute(accessToken, userClient.getUser("user"));
		model.addAttribute("activeMenu", "profile");
		return "mypage/profile";
	}

	@PutMapping("/profile")
	public String updateProfile(@CookieValue("accessToken") String accessToken,
							 UserUpdateDto userUpdateDto,
							 Model model) {
		userClient.updateUser(accessToken, userUpdateDto);
		model.addAttribute("activeMenu", "profile");
		return "redirect:mypage/profile";
	}

	@GetMapping("/addresses")
	public String getAddresses(@CookieValue("accessToken") String accessToken,
							   Model model) {
		model.addAttribute("addresses", userClient.getAddresses(accessToken));
		model.addAttribute("activeMenu", "addresses");
		return "mypage/addresses";
	}

	@PostMapping("/addresses")
	public String createAddress(@CookieValue("accessToken") String accessToken,
								UserAddressDto userAddressDto) {
		userClient.createUserAddress(accessToken, userAddressDto);
		return "redirect:/mypage/addresses";
	}

	@PutMapping("/addresses/{address-id}")
	public String updateAddress(@CookieValue("accessToken") String accessToken,
								@PathVariable("address-id") Long addressId,
								UserAddressDto userAddressDto) {
		userClient.updateUserAddress(accessToken, addressId, userAddressDto);
		return "redirect:/mypage/addresses";
	}

	@DeleteMapping("/addresses/{address-id}")
	public String deleteAddress(@CookieValue("accessToken") String accessToken,
								@PathVariable("address-id") Long addressId) {
		userClient.deleteAddress(accessToken, addressId);
		return "redirect:/mypage/addresses";
	}

	@GetMapping("/points")
	public String getPointHistories(@CookieValue("accessToken") String accessToken,
							@PageableDefault Pageable pageable,
							Model model) {
		model.addAttribute("points", userClient.getPointHistories(accessToken, pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort()));
		model.addAttribute("activeMenu", "points");
		return "mypage/points";
	}

	@GetMapping("/orders")
	public String getOrders(@CookieValue("accessToken") String accessToken,
							Model model) {
		model.addAttribute("orders", orderClient.getOrders(accessToken));
		model.addAttribute("activeMenu", "orders");
		return "mypage/orders";
	}

}