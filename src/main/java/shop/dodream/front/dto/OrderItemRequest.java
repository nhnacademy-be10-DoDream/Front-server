package shop.dodream.front.dto;

import lombok.Data;

@Data
public class OrderItemRequest {
    private Long bookId;
    private String title;
    private Integer quantity;
    private Integer price;
    private Long wrappingId;
    private WrappingDto wrappingInfo;
    private Long couponId;
    private Integer finalPrice;
}
