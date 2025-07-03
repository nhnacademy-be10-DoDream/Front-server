package shop.dodream.front.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;
import shop.dodream.front.client.CartClient;
import shop.dodream.front.client.OrderClient;
import shop.dodream.front.dto.GuestCartItemRequest;
import shop.dodream.front.dto.GuestCartResponse;
import shop.dodream.front.dto.WrappingDto;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/guest-cart")
public class GuestCartController {
	private final CartClient cartClient;
	private final OrderClient orderClient;
	
	
	@GetMapping
	public String guestCartPage(HttpServletRequest request, Model model) {
		String guestId = getGuestId(request);
		GuestCartResponse response = cartClient.getGuestCart(guestId);
		model.addAttribute("cart", response);
		
		List<WrappingDto> wrappingOptions = orderClient.getGiftWraps();
		model.addAttribute("wrappingOptions", wrappingOptions);
		return "guest-cart";
	}
	
	@PostMapping("/add")
	public String addCartItem(@ModelAttribute GuestCartItemRequest request, HttpServletRequest servletRequest) {
		String guestId = getGuestId(servletRequest);
		cartClient.addGuestCartItem(guestId, request);
		return "redirect:/guest-cart";
	}
	
	@PostMapping("/delete/{bookId}")
	public String deleteCartItem(@PathVariable Long bookId, HttpServletRequest servletRequest) {
		String guestId = getGuestId(servletRequest);
		cartClient.deleteGuestCartItem(guestId, bookId);
		return "redirect:/guest-cart";
	}
	
	@PutMapping("/update/{bookId}")
	public String updateQuantity(@PathVariable Long bookId,
	                             @RequestParam("quantity") Long quantity,
	                             HttpServletRequest request) {
		String guestId = getGuestId(request);
		
		GuestCartItemRequest requestDto = new GuestCartItemRequest();
		requestDto.setBookId(bookId);
		requestDto.setQuantity(quantity);
		
		cartClient.updateGuestCartItem(guestId, requestDto);
		
		return "redirect:/guest-cart";
	}
	
	private String getGuestId(HttpServletRequest request) {
		Cookie cookie = WebUtils.getCookie(request, "guestId");
		return cookie != null ? cookie.getValue() : null;
	}
}
