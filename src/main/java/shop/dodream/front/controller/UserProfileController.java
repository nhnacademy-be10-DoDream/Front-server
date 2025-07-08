package shop.dodream.front.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import shop.dodream.front.client.BookClient;
import shop.dodream.front.client.CouponClient;
import shop.dodream.front.client.OrderClient;
import shop.dodream.front.client.UserClient;
import shop.dodream.front.dto.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class UserProfileController {
	private final UserClient userClient;
	private final OrderClient orderClient;
    private final CouponClient couponClient;
	private final BookClient bookClient;

	@Value("${app.image.review-prefix}")
	private String reviewPrefix;
	@Value("${app.image.book-prefix}")
	private String bookPrefix;

	@GetMapping(path = {"/profile", ""})
	public String getProfile(Model model) {
		model.addAttribute("currentUser",userClient.getUser());
		model.addAttribute("activeMenu", "profile");
		return "/mypage/profile";
	}

	@PutMapping("/profile")
	public String updateProfile(UserUpdateDto userUpdateDto,
								Model model) {
		model.addAttribute("currentUser", userClient.updateUser( userUpdateDto));
		model.addAttribute("activeMenu", "profile");
		return "/mypage/profile";
	}

	@PutMapping("/password")
	public String updatePassword(UserPasswordUpdateDto userPasswordUpdateDto,
								 Model model) {
		model.addAttribute("currentUser", userClient.updateUser(userPasswordUpdateDto));
		model.addAttribute("activeMenu", "profile");
		return "/mypage/profile";
	}

	@PostMapping("/profile")
	public String deleteAccount() {
		userClient.deleteUser();

		return "forward:/auth/logout";
	}

	@GetMapping("/addresses")
	public String getAddresses(Model model) {
		model.addAttribute("addresses", userClient.getAddresses());
		model.addAttribute("activeMenu", "addresses");
		return "/mypage/addresses";
	}

	@PostMapping("/addresses")
	public String createAddress(UserAddressDto userAddressDto) {
		userClient.createUserAddress( userAddressDto);
		return "redirect:/mypage/addresses";
	}

	@PutMapping("/addresses/{address-id}")
	public String updateAddress(@PathVariable("address-id") Long addressId,
								UserAddressDto userAddressDto) {
		userClient.updateUserAddress( addressId, userAddressDto);
		return "redirect:/mypage/addresses";
	}

	@DeleteMapping("/addresses/{address-id}")
	public String deleteAddress(@PathVariable("address-id") Long addressId) {
		userClient.deleteAddress( addressId);
		return "redirect:/mypage/addresses";
	}

	@GetMapping("/points")
	public String getPointHistories(@PageableDefault Pageable pageable,
									Model model) {
		model.addAttribute("points", userClient.getPointHistories( pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort()));
		model.addAttribute("activeMenu", "points");
		return "/mypage/points";
	}

	@GetMapping("/orders")
	public String getOrders(Model model) {
		model.addAttribute("orders", orderClient.getOrders());
		model.addAttribute("activeMenu", "orders");
		return "/mypage/orders";
	}

	@GetMapping("/coupons")
	public String getMyCoupons(Model model) {
		List<AvailableCouponResponse> availableCoupons = couponClient.getAvailableCoupons();
		model.addAttribute("availableCoupons", availableCoupons);
		model.addAttribute("activeMenu", "coupons");
		return "/mypage/coupons";
	}

	@GetMapping("/reviews")
	public String getReviews(@PageableDefault(size = 5) Pageable pageable,
							 Model model) {
		model.addAttribute("reviews", bookClient.getReviews(pageable));
		model.addAttribute("reviewPrefix", reviewPrefix);
		model.addAttribute("activeMenu", "reviews");
		return "/mypage/reviews";
	}

	@GetMapping("/liked-books")
	public String getLikedBooks(@PageableDefault(size = 5) Pageable pageable,
								Model model) {
		model.addAttribute("likedBooks", bookClient.getLikedBooks(pageable));
		model.addAttribute("bookPrefix", bookPrefix);
		model.addAttribute("activeMenu", "liked-books");
		return "/mypage/liked-books";
	}

}