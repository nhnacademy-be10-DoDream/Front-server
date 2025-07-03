package shop.dodream.front.dto;

import lombok.Data;

@Data
public class OrderItemDto {
    private Long bookId;
    private String title;
    private Integer quantity;
    private Integer price;
    private WrappingDto wrappingInfo;
}
