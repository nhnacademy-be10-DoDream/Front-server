package shop.dodream.front.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import shop.dodream.front.client.CouponClient;
import shop.dodream.front.dto.CouponResponse;
import shop.dodream.front.dto.IssueCouponToUsersRequest;
import shop.dodream.front.dto.UserSearchCondition;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/user-coupons")
public class UserCouponController {

    private final CouponClient couponClient;

    @GetMapping("/issue")
    public String issueCouponForm(Model model, Pageable pageable) {
        model.addAttribute("activeMenu", "issue-coupon");
        List<CouponResponse> coupons = couponClient.getAllCoupons(pageable).getContent();
        model.addAttribute("coupons", coupons);
        return "admin/usercoupon/issue";
    }

    @PostMapping("/issue")
    public String issueCoupon(@RequestParam("couponId") Long couponId,
                              @RequestParam(value = "userIds", required = false) String userIds,
                              @RequestParam(value = "grade", required = false) String grade,
                              @RequestParam(value = "birthMonth", required = false) Integer birthMonth) {

        List<String> userIdList = null;
        if (userIds != null && !userIds.trim().isEmpty()) {
            userIdList = List.of(userIds.split(","));
        }

        UserSearchCondition condition = new UserSearchCondition();
        condition.setGrade(grade);
        condition.setBirthMonth(birthMonth);

        IssueCouponToUsersRequest issueCouponToUsersRequest = new IssueCouponToUsersRequest(couponId, userIdList, condition);
        couponClient.issueCouponsToUsers(issueCouponToUsersRequest);
        return "redirect:/admin/user-coupons/issue";
    }
}