package shop.dodream.front.dto;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class OrderByStatusResponse {
    private String orderId;
    private String orderStatus; // 주문 상태
    private ZonedDateTime orderAt; // 주문 시간
}
