package shop.dodream.front.dto;

import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BookCouponRequest {
	
	@NotNull
	private Long bookId;
	
	private Long userCouponId;
}