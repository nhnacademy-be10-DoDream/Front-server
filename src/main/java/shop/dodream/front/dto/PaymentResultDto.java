package shop.dodream.front.dto;

import lombok.Data;

@Data
public class PaymentResultDto {
    private String orderId;
    private String amount;
    private String method;
    private String address;
    private String items;
}
