
package shop.dodream.front.dto;

import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;

@Data
public class OrderDetailsDto {
    private String orderId;
    private String status;
    private ZonedDateTime orderDate;
    private ZonedDateTime wantedDate;
//    private int totalPrice;
//    private int pointUsed;
//

    private String guestName;
    private String guestPhone;

    private UserAddressDto address;
    private PaymentDto payment;
    private List<OrderItemDto> items;
}