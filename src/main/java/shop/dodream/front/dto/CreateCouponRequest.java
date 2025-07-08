package shop.dodream.front.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateCouponRequest {
    private Long policyId;
    private Long bookId;
    private Long categoryId;
}