package shop.dodream.front.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponResponse {
    private Long couponId;
    private Long policyId;
    private String policyName;
    private String type;
    private Long discountValue;
    private Long minPurchaseAmount;
    private Long maxDiscountAmount;
    private Long bookId;
    private Long categoryId;
}