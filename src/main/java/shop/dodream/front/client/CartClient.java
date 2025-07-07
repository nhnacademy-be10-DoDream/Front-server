package shop.dodream.front.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import shop.dodream.front.dto.*;

import java.util.List;

@FeignClient(name = "cartClient", url = "http://localhost:10320")
public interface CartClient {
	
	@GetMapping("/carts/users")
	CartResponse getCart();
	
	@PostMapping("/carts")
	CartResponse createCart( @RequestParam(value = "guestId", required = false) String guestId);
	
	@DeleteMapping("/carts/{cartId}")
	void deleteCart(@PathVariable Long cartId);
	
	@PostMapping("/carts/merge")
	void mergeCart();
	
	@GetMapping("/carts/{cartId}/cart-items")
	List<CartItemResponse> getCartItems(@PathVariable Long cartId);
	
	@PostMapping("/carts/{cartId}/cart-items")
	CartItemResponse addCartItem(@PathVariable Long cartId,
	                             @RequestBody CartItemRequest request);
	
	@PutMapping("/carts/{cartId}/cart-items/{cartItemId}/quantity")
	CartItemResponse updateCartItemQuantity(@RequestBody CartItemRequest request,
	                                        @PathVariable Long cartItemId, @PathVariable Long cartId);
	
	@DeleteMapping("/carts/cart-items/{cartItemId}")
	void deleteCartItem(@PathVariable Long cartItemId);
	
	@GetMapping("/public/carts")
	GuestCartResponse getPublicCart();
	
	@GetMapping("/public/carts/{guestId}")
	GuestCartResponse getGuestCart(@PathVariable("guestId") String guestId);
	
	@PostMapping("/public/carts/{guestId}/cart-items")
	GuestCartItemResponse addGuestCartItem(@PathVariable String guestId,@RequestBody GuestCartItemRequest request);
	
	@DeleteMapping("/public/carts/{guestId}/cart-items/books/{bookId}")
	void deleteGuestCartItem(@PathVariable String guestId,@PathVariable Long bookId);
	
	@PutMapping("/public/carts/{guestId}/quantity")
	GuestCartResponse updateGuestCartItem(@PathVariable String guestId,@RequestBody GuestCartItemRequest request);
}
