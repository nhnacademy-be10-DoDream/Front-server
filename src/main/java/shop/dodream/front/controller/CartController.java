package shop.dodream.front.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import shop.dodream.front.client.CartClient;
import shop.dodream.front.client.CouponClient;
import shop.dodream.front.dto.CartItemResponse;
import shop.dodream.front.dto.CartResponse;
import shop.dodream.front.dto.CouponResponse;
import shop.dodream.front.dto.GuestCartResponse;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {
	
	private final CouponClient couponClient;
	private final CartClient cartClient;
	
	@GetMapping
	public String showCart(Model model, HttpServletRequest request) {
		String userId = request.getHeader("X-USER-ID");
		String guestId = getCookieHeader(request);
		if (userId == null) {
			GuestCartResponse Guestcart = cartClient.getGuestCart(request.getHeader("Cookie"));
		}
		
		CartResponse cart = cartClient.getCart(userId);
		
		List<CartItemResponse> cartItems = cartClient.getCartItems(cart.getCartId());
		model.addAttribute("cartItems", cartItems);
		
		// 각 카트 아이템에 대해 사용 가능한 쿠폰 조회
		Map<Long, List<CouponResponse>> couponsMap = new HashMap<>();
		for (CartItemResponse item : cartItems) {
			List<CouponResponse> coupons = couponClient.getAvailableCoupons(item.getBookId(), item.getSalePrice());
			couponsMap.put(item.getCartItemId(), coupons);
		}
		model.addAttribute("couponsMap", couponsMap);
		
		return "cart";
	}
	
	private String getCookieHeader(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies == null) return "";
		
		return Arrays.stream(cookies)
				       .map(cookie -> cookie.getName() + "=" + cookie.getValue())
				       .collect(Collectors.joining("; "));
	}
}