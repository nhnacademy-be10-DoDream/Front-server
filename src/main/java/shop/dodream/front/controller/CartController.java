package shop.dodream.front.controller;

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
@RequestMapping("/cart")
public class CartController {
	
	private final CouponClient couponClient;
	private final CartClient cartClient;
	private final OrderClient orderClient;
	
	@GetMapping
	public String showCart(Model model) {
		CartResponse cart = cartClient.getCart();
		// 유저가 아이디로 카트를 조회하고 없다면 만드는 로직 추가 예정
		List<CartItemResponse> cartItems = cartClient.getCartItems(cart.getCartId());
		model.addAttribute("cartItems", cartItems);
		
		// 각 카트 아이템에 대해 사용 가능한 쿠폰 조회
		Map<Long, List<CouponResponse>> couponsMap = new HashMap<>();
		for (CartItemResponse item : cartItems) {
			List<CouponResponse> coupons = couponClient.getAvailableCoupons(item.getBookId(), item.getSalePrice());
			couponsMap.put(item.getCartItemId(), coupons);
		}
		model.addAttribute("couponsMap", couponsMap);
		
		List<WrappingDto> wrappingOptions = orderClient.getGiftWraps();
		model.addAttribute("wrappingOptions", wrappingOptions);
		
		return "cart";
	}
	
	@PostMapping
	public String addCartItem(@ModelAttribute CartItemRequest request) {
		CartResponse cart = cartClient.getCart(); // 사용자 장바구니 조회
		cartClient.addCartItem(cart.getCartId(), request);
		return "redirect:/cart";
	}
	
	@PutMapping("/{cartItemId}")
	public String updateQuantity(@PathVariable Long cartItemId,
	                             @RequestParam Long quantity) {
		CartResponse cart = cartClient.getCart();
		CartItemRequest request = new CartItemRequest();
		request.setQuantity(quantity);
		cartClient.updateCartItemQuantity(request, cartItemId, cart.getCartId());
		return "redirect:/cart";
	}
	
	@DeleteMapping("/{cartItemId}")
	public String deleteCartItem(@PathVariable Long cartItemId) {
		cartClient.deleteCartItem(cartItemId);
		return "redirect:/cart";
	}
}