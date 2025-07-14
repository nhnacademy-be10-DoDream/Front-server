package shop.dodream.front.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;


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
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime fixedDate;
    private Long plusDay;
}