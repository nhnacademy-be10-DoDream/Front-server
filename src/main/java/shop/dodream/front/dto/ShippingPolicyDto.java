package shop.dodream.front.dto;

import lombok.Data;

@Data
public class ShippingPolicyDto {
    private Long shippingId;
    private String name;
    private Integer fee;
    private Integer minimumAmount;
    private boolean isActive;
}
