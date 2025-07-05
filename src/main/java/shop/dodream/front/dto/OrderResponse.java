package shop.dodream.front.dto;

import java.time.ZonedDateTime;
import java.util.List;

public record OrderResponse(
        String orderId,
        String status,
        int totalPrice,
        int pointUsed,
        int couponDiscount,
        int paymentAmount,
        ZonedDateTime orderAt,
        List<OrderItemResponse> orderItems
) {}
