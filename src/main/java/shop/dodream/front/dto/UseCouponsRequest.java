package shop.dodream.front.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class UseCouponsRequest {
    private List<Long> userCouponIds;
}
