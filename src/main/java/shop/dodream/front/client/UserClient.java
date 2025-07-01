package shop.dodream.front.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import shop.dodream.front.dto.*;

import java.util.List;

//fixme : 배포전 주석 수정 필요
//@FeignClient(name = "userClient", url = "http://localhost:10320")
@FeignClient(name = "userClient", url = "s1.java21.net:10320")
public interface UserClient {
    @PostMapping("/users/signup")

    void createUserAccount(SignupRequest signupRequest);

    @PostMapping("/users/{user-id}/address")
    void createUserAddress(@PathVariable("user-id") String userId, @RequestBody UserAddressDto userAddressDto);

    @GetMapping("/users/me")
    UserDto getUser(@CookieValue("accessToken") String accessToken);

    @GetMapping("/users/me/addresses")
    List<UserAddressResponse> getAddresses(@CookieValue("accessToken") String accessToken);

    @GetMapping("/users/me/addresses/{address-id}")
    UserAddressResponse getAddress(@CookieValue("accessToken") String accessToken, @PathVariable("address-id") Long addressId);
}