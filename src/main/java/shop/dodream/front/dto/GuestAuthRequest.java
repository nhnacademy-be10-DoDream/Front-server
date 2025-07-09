package shop.dodream.front.dto;

import lombok.Data;

@Data
public class GuestAuthRequest {
    private String guestPhone;
    private String guestPassword;
}
