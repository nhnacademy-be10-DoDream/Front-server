package shop.dodream.front.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import shop.dodream.front.dto.CartItem;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {
	
	@GetMapping
	public String showCart(Model model) {
		List<CartItem> cartItems = Arrays.asList(
				new CartItem(1L, "스프링 부트 책", "https://via.placeholder.com/48", 20000, 15000, 2),
				new CartItem(2L, "자바의 정석", "https://via.placeholder.com/48", 30000, 25000, 1)
		);
		
		model.addAttribute("cartItems", cartItems);
		return "cart";
	}
}