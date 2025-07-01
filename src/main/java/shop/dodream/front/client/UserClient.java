package shop.dodream.front.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import shop.dodream.front.dto.*;

@FeignClient(name = "userClient", url = "http://localhost:10320")
public interface UserClient {
    @PostMapping("/users/signup")
    void createUserAccount(SignupRequest signupRequest);

    @PostMapping("/users/{user-id}/address")
    void createUserAddress(@PathVariable("user-id") String userId, @RequestBody UserAddressDto userAddressDto);

    @GetMapping("/users/me")
    UserDto getUser(@CookieValue("accessToken") String accessToken);
}
