package shop.dodream.front.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserCouponResponse {

    private  Long id;
    private  String userId;
    private  CouponResponse coupon;
    private  ZonedDateTime issuedAt;
    private  ZonedDateTime usedAt;
    private  ZonedDateTime expiredAt;
}
