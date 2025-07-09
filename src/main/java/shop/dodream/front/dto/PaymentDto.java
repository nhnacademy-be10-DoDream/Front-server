package shop.dodream.front.dto;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class PaymentDto {
    private String method;
    private int amount;
    private ZonedDateTime paidAt;
}
