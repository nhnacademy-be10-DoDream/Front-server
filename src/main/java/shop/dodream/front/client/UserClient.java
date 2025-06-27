package shop.dodream.front.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import shop.dodream.front.dto.CreateAccountRequest;
import shop.dodream.front.dto.CreateAccountResponse;
import shop.dodream.front.dto.UserAddressDto;

@FeignClient(name = "userClient", url = "http://localhost:10320")
public interface UserClient {
    @PostMapping("/accounts/signup")
    CreateAccountResponse createUserAccount(CreateAccountRequest request);

    @PostMapping("/users/{user-id}/address")
    Void createUserAddress(@PathVariable("user-id") String userId, @RequestBody UserAddressDto userAddressDto);

}
