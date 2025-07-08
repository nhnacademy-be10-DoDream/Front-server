package shop.dodream.front.dto;

import lombok.Data;

@Data
public class PaymentCancelRequest {
    private String cancelReason; // 취소 사유
    private Integer cancelAmount;
}
