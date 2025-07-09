package shop.dodream.front.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import shop.dodream.front.client.CouponClient;
import shop.dodream.front.dto.CreateCouponRequest;
import shop.dodream.front.dto.CouponResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/coupons")
public class CouponController {

    private final CouponClient couponClient;

    @GetMapping
    public String getCouponList(Model model, Pageable pageable) {
        Page<CouponResponse> coupons = couponClient.getAllCoupons(pageable);
        model.addAttribute("coupons", coupons);
        model.addAttribute("activeMenu", "coupons");
        return "admin/coupon/list";
    }

    @GetMapping("/add")
    public String addCouponForm(Model model) {
        model.addAttribute("createCouponRequest", new CreateCouponRequest());
        return "admin/coupon/add";
    }

    @PostMapping("/add")
    public String createCoupon(@ModelAttribute CreateCouponRequest request) {
        couponClient.createCoupon(request);
        return "redirect:/admin/coupons";
    }

    @DeleteMapping("/delete/{couponId}")
    public String deleteCoupon(@PathVariable("couponId") Long couponId) {
        couponClient.deleteCoupon(couponId);
        return "redirect:/admin/coupons";
    }
}