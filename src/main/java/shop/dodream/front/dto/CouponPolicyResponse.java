package shop.dodream.front.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.ZonedDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class CouponPolicyResponse {
    private Long id;
    private String name;
    private String discountType;
    private Long discountValue;
    private Long minPurchaseAmount;
    private Long maxDiscountAmount;
    private String expiredStrategy;
    private ZonedDateTime fixedDate;
    private Long plusDay;
}