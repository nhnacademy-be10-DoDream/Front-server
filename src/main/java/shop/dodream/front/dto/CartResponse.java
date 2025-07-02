package shop.dodream.front.dto;

import lombok.Data;

import java.util.List;

@Data
public class CartResponse {
	private Long cartId;
	private String userId;
	private String guestId;
	private List<CartItemResponse> items;
}
