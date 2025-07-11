package shop.dodream.front.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuestCartItemResponse {
	private Long bookId;
	private String title;
	private Long quantity;
	private Long salePrice;
	private String bookUrl;
}

