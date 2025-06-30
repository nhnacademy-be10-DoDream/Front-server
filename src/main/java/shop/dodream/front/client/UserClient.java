package shop.dodream.front.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import shop.dodream.front.dto.CreateAccountRequest;
import shop.dodream.front.dto.CreateAccountResponse;
import shop.dodream.front.dto.SignupRequest;
import shop.dodream.front.dto.UserAddressDto;

@FeignClient(name = "userClient", url = "http://localhost:10320")
public interface UserClient {
    @PostMapping("/users/signup")

    void createUserAccount(SignupRequest signupRequest);

    @PostMapping("/users/{user-id}/address")
    void createUserAddress(@PathVariable("user-id") String userId, @RequestBody UserAddressDto userAddressDto);

}
