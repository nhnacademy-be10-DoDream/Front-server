package shop.dodream.front.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.time.ZonedDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateCouponPolicyRequest {
    private String name;
    private String discountType;
    private Long discountValue;
    private Long minPurchaseAmount;
    private Long maxDiscountAmount;
    private String expiredStrategy;
    private ZonedDateTime fixedDate;
    private Long plusDay;
}