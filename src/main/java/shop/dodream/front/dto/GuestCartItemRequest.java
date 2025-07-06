package shop.dodream.front.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuestCartItemRequest {
	private Long bookId;
	private Long quantity;
}
