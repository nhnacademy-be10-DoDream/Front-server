package shop.dodream.front.dto;

import lombok.Data;

@Data
public class ShippingPolicyDto {
    private Long id = 1L; // 배송 정책 ID
    private String name; // 배송 정책 이름
    private Integer fee; // 배송비
    private Integer minimumAmount; // 최소 주문 금액
    private boolean isActive; // 활성화 여부
}
