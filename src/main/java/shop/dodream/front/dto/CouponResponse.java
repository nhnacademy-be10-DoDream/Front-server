package shop.dodream.front.dto;

import lombok.Data;

@Data
public class CouponResponse {
	private Long couponId;
	private String couponName;
	private Long discountValue;
	private Long minPurchaseAmount;
	private Long maxDiscountAmount;
	private Long finalPrice;
}