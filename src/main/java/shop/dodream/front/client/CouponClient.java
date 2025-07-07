package shop.dodream.front.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import shop.dodream.front.dto.*;

import java.util.List;

@FeignClient(name = "coupon", url = "${gateway.url}")
public interface CouponClient {

    // Coupon APIs
    @PostMapping("/admin/coupons")
    void createCoupon(@RequestBody CreateCouponRequest request);

    @GetMapping("/admin/coupons/{couponId}")
    CouponResponse getCoupon(@PathVariable("couponId") Long couponId);

    @GetMapping("/admin/coupons")
    Page<CouponResponse> getAllCoupons(Pageable pageable);

    @DeleteMapping("/admin/coupons/{couponId}")
    void deleteCoupon(@PathVariable("couponId") Long couponId);

    // Coupon Policy APIs
    @PostMapping("/admin/policies")
    void createCouponPolicy(@RequestBody CreateCouponPolicyRequest request);

    @GetMapping("/admin/policies/{policyId}")
    CouponPolicyResponse getCouponPolicy(@PathVariable("policyId") Long policyId);

    @DeleteMapping("/admin/policies/{policyId}")
    void deleteCouponPolicy(@PathVariable("policyId") Long policyId);

    @PutMapping("/admin/policies/{policyId}")
    void updateCouponPolicy(@PathVariable("policyId") Long policyId, @RequestBody UpdateCouponPolicyRequest request);

    @GetMapping("/admin/policies")
    List<CouponPolicyResponse> getAllCouponPolicies();

    @GetMapping("/admin/policies/{policyId}/coupons")
    List<CouponResponse> getCouponsByPolicy(@PathVariable("policyId") Long policyId);

    // User Coupon APIs
    @PostMapping("/admin/user-coupons")
    void issueCoupon(@RequestBody IssueCouponRequest request);

    @PostMapping("/admin/user-coupons/issue_multiple")
    void issueCouponsToUsers(@RequestBody IssueCouponToUsersRequest request);

    @GetMapping("/coupons/me/available")
    List<AvailableCouponResponse> getAvailableCoupons();

    @PutMapping("/coupons/me/{user-coupon-id}/use")
    void useCoupon(@PathVariable("user-coupon-id") Long userCouponId);

    @PutMapping("/coupons/me/{user-coupon-id}/apply")
    void applyCoupon(@PathVariable("user-coupon-id") Long userCouponId);

    @PutMapping("/coupons/me/use-multiple")
    void useMultipleCoupons(@RequestBody UseCouponsRequest request);

    @GetMapping("/coupons/book/{book-id}")
    List<BookAvailableCouponResponse> getAvailableCouponsforBook(@PathVariable("book-id") Long bookId, @RequestParam("bookPrice") Long bookPrice);

}
