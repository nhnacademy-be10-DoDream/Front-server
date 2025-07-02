package shop.dodream.front.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CartItemResponse {
	private Long cartItemId;
	private Long bookId;
	private String title;
	private Long salePrice;
	private Long quantity;
	private String bookUrl;
}