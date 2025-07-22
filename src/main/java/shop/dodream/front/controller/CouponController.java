package shop.dodream.front.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import shop.dodream.front.client.BookClient;
import shop.dodream.front.client.CouponClient;
import shop.dodream.front.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/coupons")
public class CouponController {

    private final CouponClient couponClient;
    private final BookClient bookClient;

    @GetMapping
    public String getCouponList(Model model, Pageable pageable) {
        Page<CouponResponse> coupons = couponClient.getAllCoupons(pageable);
        model.addAttribute("coupons", coupons);
        model.addAttribute("activeMenu", "coupons");
        return "admin/coupon/list";
    }

    @GetMapping("/add")
    public String addCouponForm(@RequestParam(value = "policyId", required = false) Long policyId, Model model) throws JsonProcessingException {
        CreateCouponRequest request = new CreateCouponRequest();
        if (policyId != null) {
            request.setPolicyId(policyId);
        }

        List<CategoryResponse> categories = bookClient.getAllCategories();
        ObjectMapper mapper = new ObjectMapper();
        String categoryJson = mapper.writeValueAsString(categories);

        model.addAttribute("categoryList", categories);
        model.addAttribute("categoryJson", categoryJson);
        model.addAttribute("createCouponRequest", request);
        model.addAttribute("couponPolicies", couponClient.getAllCouponPolicies());
        return "admin/coupon/add";
    }

    @PostMapping("/add")
    public String createCoupon(@RequestParam("policyId") Long policyId,
                               @RequestParam(value = "isbn", required = false) String isbn,
                               @RequestParam(value = "categoryId", required = false) Long categoryId){

        Long bookId = null;
        if (isbn != null && !isbn.trim().isEmpty()) {
            BookItemResponse response = bookClient.getBookByIsbn(isbn);
            bookId = response.getBookId();
        }
        CreateCouponRequest request = new CreateCouponRequest(policyId, bookId, categoryId);
        couponClient.createCoupon(request);
        return "redirect:/admin/coupons";
    }

    @GetMapping("/{couponId}")
    public String getCouponDetail(@PathVariable("couponId") Long couponId, Model model) {
        List<UserCouponResponse> userCoupons = couponClient.getUserCouponsByCoupon(couponId);
        model.addAttribute("userCoupons", userCoupons);
        model.addAttribute("couponId", couponId);
        model.addAttribute("activeMenu", "coupons");
        return "admin/coupon/detail";
    }

    @DeleteMapping("/delete/{couponId}")
    public String deleteCoupon(@PathVariable("couponId") Long couponId) {
        couponClient.deleteCoupon(couponId);
        return "redirect:/admin/coupons";
    }

    @DeleteMapping("/{couponId}/user-coupons")
    public String deleteUserCouponsByCoupon(@PathVariable("couponId") Long couponId) {
        couponClient.deleteUserCouponsByCoupon(couponId);
        return "redirect:/admin/coupons/" + couponId;
    }
}