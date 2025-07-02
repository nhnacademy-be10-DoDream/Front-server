package shop.dodream.front.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import shop.dodream.front.dto.CartItemResponse;
import shop.dodream.front.dto.CartResponse;
import shop.dodream.front.dto.GuestCartResponse;

import java.util.List;

@FeignClient(name = "cart", url = "http://localhost:10320")
public interface CartClient {
	
	@GetMapping("/carts/users")
	CartResponse getCart(@RequestHeader("X-USER-ID") String userId);
	
	
	@GetMapping("/carts/{cartId}/cart-items")
	List<CartItemResponse> getCartItems(@PathVariable Long cartId);
	
	@GetMapping("/public/carts")
	GuestCartResponse getGuestCart(@RequestHeader("Cookie") String guestId);
}
