package shop.dodream.front.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.dodream.front.dto.*;

import java.util.List;

@FeignClient(name = "userClient", url = "s1.java21.net:10320")
public interface UserClient {
    @PostMapping("/public/users/signup")
    void createUserAccount(SignupRequest signupRequest);

    @GetMapping("/users/me")
    UserDto getUser();

    @PutMapping("/users/me")
    void updateUser(@RequestBody UserUpdateDto userUpdateDto);

    @PostMapping("/users/me/addresses")
    void createUserAddress(@RequestBody UserAddressDto userAddressDto);

    @PutMapping("/users/me/addresses/{address-id}")
    void updateUserAddress(@PathVariable("address-id") Long addressId,
                           @RequestBody UserAddressDto userAddressDto);

    @GetMapping("/users/me/addresses")
    List<UserAddressResponse> getAddresses();

    @DeleteMapping("/users/me/addresses/{address-id}")
    ResponseEntity<Void> deleteAddress(@PathVariable("address-id") Long addressId);

    @GetMapping("/users/me/point-histories")
    Page<PointHistoryResponse> getPointHistories(@RequestParam("page") int page,
                                                 @RequestParam("size") int size,
                                                 @RequestParam(value = "sort", required = false) Sort sort);

    @GetMapping("/users/me/point-histories/{point-history-id}")
    PointHistoryResponse getPointHistory(@PathVariable("point-history-id") Long pointHistoryId);
}