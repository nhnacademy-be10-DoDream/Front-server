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
	
	
	public static GuestCartItemResponse of(Long quantity, BookDto book) {
		
		return new GuestCartItemResponse(
				book != null ? book.getBookId() : null,
				book != null ? book.getTitle() : null,
				quantity,
				book != null ? book.getSalePrice() : null,
				book != null ? book.getBookUrl() : null
		);
	}
}

