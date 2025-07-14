package shop.dodream.front.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import shop.dodream.front.client.CouponClient;
import shop.dodream.front.dto.CouponPolicyResponse;
import shop.dodream.front.dto.CouponResponse;
import shop.dodream.front.dto.CreateCouponPolicyRequest;
import shop.dodream.front.dto.UpdateCouponPolicyRequest;

import java.util.Date;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/policies")
public class CouponPolicyController {

    private final CouponClient couponClient;

    @GetMapping
    public String getPolicyList(Model model) {
        List<CouponPolicyResponse> policies = couponClient.getAllCouponPolicies();
        model.addAttribute("policies", policies);
        model.addAttribute("activeMenu", "policies");
        return "admin/couponpolicy/list";
    }

    @GetMapping("/{policyId}")
    public String getPolicyDetail(@PathVariable("policyId") Long policyId, Model model) {
        List<CouponResponse> coupons = couponClient.getCouponsByPolicy(policyId);
        model.addAttribute("coupons", coupons);
        model.addAttribute("policyId", policyId);
        model.addAttribute("activeMenu", "policies");
        return "admin/couponpolicy/detail";
    }

    @GetMapping("/add")
    public String addPolicyForm(Model model) {
        model.addAttribute("createCouponPolicyRequest", new CreateCouponPolicyRequest());
        return "admin/couponpolicy/add";
    }

    @PostMapping("/add")
    public String createPolicy(@ModelAttribute CreateCouponPolicyRequest request) {
        couponClient.createCouponPolicy(request);
        return "redirect:/admin/policies";
    }

    @GetMapping("/edit/{policyId}")
    public String editPolicyForm(@PathVariable("policyId") Long policyId, Model model) {
        CouponPolicyResponse policy = couponClient.getCouponPolicy(policyId);
        model.addAttribute("policy", policy);
        model.addAttribute("updateCouponPolicyRequest", new UpdateCouponPolicyRequest());
        return "admin/couponpolicy/edit";
    }

    @PutMapping("/edit/{policyId}")
    public String updatePolicy(@PathVariable("policyId") Long policyId, @ModelAttribute UpdateCouponPolicyRequest request) {
        couponClient.updateCouponPolicy(policyId, request);
        return "redirect:/admin/policies";
    }

    @DeleteMapping("/delete/{policyId}")
    public String deletePolicy(@PathVariable("policyId") Long policyId) {
        couponClient.deleteCouponPolicy(policyId);
        return "redirect:/admin/policies";
    }
}