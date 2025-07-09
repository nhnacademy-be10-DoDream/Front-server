package shop.dodream.front.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import shop.dodream.front.client.CartClient;
import shop.dodream.front.client.CouponClient;
import shop.dodream.front.client.OrderClient;
import shop.dodream.front.dto.*;
import java.util.List;


@Controller
@RequiredArgsConstructor
public class CartController {
	
	private final CouponClient couponClient;
	private final CartClient cartClient;
	private final OrderClient orderClient;
	
	@GetMapping("/cart")
	public String showCart(HttpServletRequest request, Model model) {
		String accessToken = getAccessTokenFromCookies(request.getCookies());
		
		if (accessToken == null || accessToken.isEmpty()) {
			// 비회원 장바구니 페이지
			String guestId = getGuestIdFromCookie(request);
			if (guestId != null) {
				GuestCartResponse guestCartResponse = cartClient.getGuestCart(guestId);
				List<GuestCartItemResponse> guestCartItemResponses = guestCartResponse.getItems();
				model.addAttribute("cartItems", guestCartItemResponses);
				
				List<WrappingDto> wrappingOptions = orderClient.getGiftWraps();
				model.addAttribute("wrappingOptions", wrappingOptions);
				
				return "guest-cart";
				
			}
			GuestCartResponse guestCartResponse = cartClient.getPublicCart();
			List<GuestCartItemResponse> guestCartItemResponses = guestCartResponse.getItems();
			model.addAttribute("cartItems", guestCartItemResponses);
			
			List<WrappingDto> wrappingOptions = orderClient.getGiftWraps();
			model.addAttribute("wrappingOptions", wrappingOptions);
			
			return "guest-cart";
		}
		
		// 회원 장바구니 처리
		CartResponse cart = cartClient.getCart(); // 사용자 장바구니 조회
		List<CartItemResponse> cartItems = cartClient.getCartItems(cart.getCartId());
		model.addAttribute("cartItems", cartItems);
		
		List<WrappingDto> wrappingOptions = orderClient.getGiftWraps();
		model.addAttribute("wrappingOptions", wrappingOptions);
		
		return "cart";
	}

	@PostMapping("/cart/add")
	public String addCartItem(@ModelAttribute CartItemRequest request, HttpServletRequest httpServletRequest, HttpServletResponse response) {
		String accessToken = getAccessTokenFromCookies(httpServletRequest.getCookies());
		if (accessToken == null || accessToken.isEmpty()) {
			String guestId = getGuestIdFromCookie(httpServletRequest);
			if(guestId == null){
				GuestCartResponse guestCartResponse = cartClient.getPublicCart();
				guestId = guestCartResponse.getGuestId();
				Cookie cookie = new Cookie("guestId", guestId);
				cookie.setPath("/");
				cookie.setMaxAge(60 * 60 * 24 * 30); // 30일
				response.addCookie(cookie);
			}
			GuestCartItemRequest guestRequest = new GuestCartItemRequest();
			guestRequest.setBookId(request.getBookId());
			guestRequest.setQuantity(request.getQuantity());
			cartClient.addGuestCartItem(guestId, guestRequest);
			return "redirect:/cart";
		}
		CartResponse cart = cartClient.getCart(); // 사용자 장바구니 조회
		cartClient.addCartItem(cart.getCartId(), request);
		return "redirect:/cart";
	}
	
	@PutMapping("/cart/{cartItemId}")
	public String updateQuantity(@PathVariable Long cartItemId,
	                             @RequestParam Long quantity) {
		CartResponse cart = cartClient.getCart();
		CartItemRequest request = new CartItemRequest();
		request.setQuantity(quantity);
		cartClient.updateCartItemQuantity(request, cartItemId, cart.getCartId());
		return "redirect:/cart";
	}
	
	@PostMapping("/cart/{bookId}")
	public String deleteCartItem(@PathVariable Long bookId) {
		CartResponse cart = cartClient.getCart();
		cartClient.deleteCartItemByBookId(cart.getCartId(),bookId);
		return "redirect:/cart";
	}
	
	@PutMapping("/guest-cart/update/{bookId}")
	public String updateGuestCartItem(@PathVariable Long bookId,
	                                  @RequestParam Long quantity,
	                                  HttpServletRequest request) {
		String guestId = getGuestIdFromCookie(request);
		GuestCartItemRequest guestRequest = new GuestCartItemRequest();
		guestRequest.setBookId(bookId);
		guestRequest.setQuantity(quantity);
		cartClient.updateGuestCartItem(guestId, guestRequest);
		return "redirect:/cart";
	}
	
	@PostMapping("/guest-cart/delete/{bookId}")
	public String deleteGuestCartItem(@PathVariable Long bookId,
	                                  HttpServletRequest request) {
		String guestId = getGuestIdFromCookie(request);
		cartClient.deleteGuestCartItem(guestId, bookId);
		return "redirect:/cart";
	}
	
	@GetMapping("/cart/coupons")
	@ResponseBody
	public List<BookAvailableCouponResponse> getAvailableCoupons(@RequestParam Long bookId) {
		return couponClient.getAvailableCouponsforBook(bookId);
	}
	
	// === 쿠키에서 accessToken 추출 ===
	private String getAccessTokenFromCookies(Cookie[] cookies) {
		if (cookies == null) return null;
		for (Cookie cookie : cookies) {
			if ("accessToken".equals(cookie.getName())) {
				return cookie.getValue();
			}
		}
		return null;
	}
	
	// === 쿠키에서 guestId 추출 ===
	private String getGuestIdFromCookie(HttpServletRequest request) {
		if (request.getCookies() != null) {
			for (Cookie cookie : request.getCookies()) {
				if ("guestId".equals(cookie.getName())) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}
	
}