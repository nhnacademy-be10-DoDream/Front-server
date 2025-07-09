package shop.dodream.front.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CartOrderRequest {
    private Integer orderTotal;
    private List<OrderItemRequest> items;
}
