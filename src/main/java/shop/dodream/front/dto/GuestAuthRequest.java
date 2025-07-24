package shop.dodream.front.dto;

import lombok.Data;

import java.io.Serializable;


@Data
public class GuestAuthRequest implements Serializable{
    private String guestPhone;
    private String guestPassword;
}
