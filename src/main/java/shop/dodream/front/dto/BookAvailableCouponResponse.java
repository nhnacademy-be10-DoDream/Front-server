package shop.dodream.front.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BookAvailableCouponResponse {
    private Long couponId;
    private String policyName;
    private Long discountValue;
    private Long minPurchaseAmount;
    private Long maxDiscountAmount;
    private Long finalPrice;
}