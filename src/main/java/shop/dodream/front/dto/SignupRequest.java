package shop.dodream.front.dto;

import lombok.Data;

@Data
public class SignupRequest {
    CreateAccountRequest userCreateRequest;
    UserAddressDto userAddressRequest;

    public SignupRequest(CreateAccountRequest request, UserAddressDto userAddressDto) {
        this.userAddressRequest = userAddressDto;
        this.userCreateRequest = request;
    }
}
