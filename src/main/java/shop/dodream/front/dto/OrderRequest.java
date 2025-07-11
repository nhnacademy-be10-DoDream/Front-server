package shop.dodream.front.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {

    private List<OrderItemRequest> orderItems;

    private String userId;
    private String receiverName;
    private String guestName;
    private String guestPhone;
    private String guestPassword;
    private String zipcode;
    private String roadAddress;
    private String detailAddress;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime wantedDate;
    private Integer totalPrice;
    private Integer pointUsed;
    private Long shippingPolicyId;
}