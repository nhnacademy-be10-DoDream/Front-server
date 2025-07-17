package shop.dodream.front.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.dodream.front.dto.*;

import java.util.List;

@FeignClient(name = "userClient", url = "${gateway.url}")
public interface UserClient {
    @PostMapping("/public/users/signup")
    void createUserAccount(SignupRequest signupRequest);

    @GetMapping("/users/me")
    UserDto getMe();

    @GetMapping("/public/users/email-check")
    boolean checkEmail(@RequestParam("email") String email);

    @GetMapping("/public/users/id-check")
    boolean checkUserId(@RequestParam("user-id") String userId);

    @PutMapping("/users/me")
    UserDto updateUser(@RequestBody UserUpdateDto userUpdateDto);

    @PutMapping("/users/me/password")
    UserDto updateUser(@RequestBody UserPasswordUpdateDto userPasswordUpdateDto);

    @DeleteMapping("/users/me")
    void deleteUser();

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
                                                 @RequestParam(value = "sort", required = false) String sort,
                                                 @RequestParam(value = "point-type", required = false) String pointType);

    @GetMapping("/users/me/point-histories/{point-history-id}")
    PointHistoryResponse getPointHistory(@PathVariable("point-history-id") Long pointHistoryId);

    @GetMapping("/users/{user-id}/points")
    Integer getAvailablePoint(@PathVariable("user-id") String userId);

    
    @GetMapping("/users/me/point-policy")
    PointPolicy getPointPolicy();
    

    @GetMapping("/public/user-grades")
    List<Grade> getGrade();

    @PutMapping("/admin/user-grades/{grade}")
    void updateGrade(@PathVariable("grade") GradeType gradeType,
                     @RequestParam("min-amount") Long minAmount);

    @GetMapping("/admin/users")
    Page<UserSimpleResponseRecord> getUsers(@SpringQueryMap UserSearchFilter filter,
                                            Pageable pageable);

    @GetMapping("/admin/users/{user-id}")
    UserDto getUser(@PathVariable("user-id") String userId);

}