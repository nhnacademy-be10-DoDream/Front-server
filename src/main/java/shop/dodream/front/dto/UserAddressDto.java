package shop.dodream.front.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserAddressDto {
    @NotBlank
    @Size(max = 50)
    private String alias;
    @NotBlank
    @Size(min = 5, max = 5)
    @Pattern(regexp = "\\d{5}")
    private String zipcode;
    @NotBlank
    @Size(max = 255)
    private String roadAddress;
    @Size(max = 255)
    private String jibunAddress;
    @Size(max = 255)
    private String extraAddress;
    @Size(max = 255)
    private String detailAddress;
}
