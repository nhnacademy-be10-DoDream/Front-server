package shop.dodream.front.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.dodream.front.dto.*;

import java.util.List;

//@FeignClient(name = "userClient", url = "https://localhost:10320")
@FeignClient(name = "userClient", url = "s1.java21.net:10320")
public interface UserClient {
    @PostMapping("/users/signup")
    void createUserAccount(SignupRequest signupRequest);

    @GetMapping("/users/me")
    UserDto getUser(@CookieValue("accessToken") String accessToken);

    @PutMapping("/users/me")
    void updateUser(@CookieValue("accessToken") String accessToken,
                    @RequestBody UserUpdateDto userUpdateDto);

    @PostMapping("/users/me/addresses")
    void createUserAddress(@CookieValue("accessToken") String accessToken,
                           @RequestBody UserAddressDto userAddressDto);

    @PutMapping("/users/me/addresses/{address-id}")
    void updateUserAddress(@CookieValue("accessToken") String accessToken,
                           @PathVariable("address-id") Long addressId,
                           @RequestBody UserAddressDto userAddressDto);

    @GetMapping("/users/me/addresses")
    List<UserAddressResponse> getAddresses(@CookieValue("accessToken") String accessToken);

    @DeleteMapping("/users/me/addresses/{address-id}")
    ResponseEntity<Void> deleteAddress(@CookieValue("accessToken") String accessToken,
                                       @PathVariable("address-id") Long addressId);

    @GetMapping("/users/me/point-histories")
    Page<PointHistoryResponse> getPointHistories(@CookieValue("accessToken") String accessToken,
                                                 @RequestParam("page") int page,
                                                 @RequestParam("size") int size,
                                                 @RequestParam(value = "sort", required = false) Sort sort);

    @GetMapping("/users/me/point-histories/{point-history-id}")
    PointHistoryResponse getPointHistory(@CookieValue("accessToken") String accessToken,
                                         @PathVariable("point-history-id") Long pointHistoryId);
}