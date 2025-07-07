package shop.dodream.front.dto;

import lombok.Data;

@Data
public class BookAvailableCouponResponse {
	private Long couponId;
	private String policyName;
	private Long discountValue;
	private Long minPurchaseAmount;
	private Long maxDiscountAmount;
	private Long finalPrice;
}