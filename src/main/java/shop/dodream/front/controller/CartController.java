package shop.dodream.front.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import shop.dodream.front.client.CartClient;
import shop.dodream.front.client.CouponClient;
import shop.dodream.front.client.OrderClient;
import shop.dodream.front.dto.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		
		// 각 카트 아이템에 대해 사용 가능한 쿠폰 조회
		Map<Long, List<BookAvailableCouponResponse>> couponsMap = new HashMap<>();
		for (CartItemResponse item : cartItems) {
			List<BookAvailableCouponResponse> coupons = couponClient.getBookAvailableCoupons(item.getBookId(), item.getSalePrice());
			couponsMap.put(item.getCartItemId(), coupons);
		}
		model.addAttribute("couponsMap", couponsMap);
		
		List<WrappingDto> wrappingOptions = orderClient.getGiftWraps();
		model.addAttribute("wrappingOptions", wrappingOptions);
		
		return "cart";
	}
	
	@PostMapping("/cart/add")
	public String addCartItem(@ModelAttribute CartItemRequest request,HttpServletRequest httpServletRequest) {
		String accessToken = getAccessTokenFromCookies(httpServletRequest.getCookies());
		if (accessToken == null || accessToken.isEmpty()) {
			String guestId = getGuestIdFromCookie(httpServletRequest);
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
	
	@DeleteMapping("/cart/{cartItemId}")
	public String deleteCartItem(@PathVariable Long cartItemId) {
		cartClient.deleteCartItem(cartItemId);
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