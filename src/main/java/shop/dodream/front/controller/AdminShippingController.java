package shop.dodream.front.controller;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import shop.dodream.front.client.OrderClient;
import shop.dodream.front.dto.ShippingPolicyDto;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/shipping-policies")
public class AdminShippingController {
    private final OrderClient orderClient;

    @GetMapping
    public String shippingPolicyList(Model model) {
        model.addAttribute("shippingPolicies", orderClient.getShippingPolicies());
        model.addAttribute("activeMenu", "shipping-policy");
        return "admin/shippingPolicy/shipping-policy-list";
    }

    @PostMapping
    public String createShippingPolicy(@ModelAttribute ShippingPolicyDto shippingPolicyDto,
                                       RedirectAttributes redirectAttributes) {
        try {
            orderClient.createShippingPolicy(shippingPolicyDto);
            redirectAttributes.addFlashAttribute("success", "배송 정책이 성공적으로 생성되었습니다!");
        } catch (FeignException e) {
            if (HttpStatus.CONFLICT.value() == e.status()) {
                redirectAttributes.addFlashAttribute("error", "이미 동일한 정책이 존재합니다!");
            }
        }
        return "redirect:/admin/shipping-policies";
    }

    @PutMapping("/{shipping-policy-id}")
    public String updateShippingPolicy(@PathVariable("shipping-policy-id") Long shippingPolicyId,
                                       @ModelAttribute ShippingPolicyDto shippingPolicyDto,
                                       RedirectAttributes redirectAttributes) {
        try {
            orderClient.updateShippingPolicy(shippingPolicyId, shippingPolicyDto);
            redirectAttributes.addFlashAttribute("success", "배송 정책이 성공적으로 수정되었습니다!");
        } catch (FeignException e) {
            if (HttpStatus.CONFLICT.value() == e.status()) {
                redirectAttributes.addFlashAttribute("error", "이미 동일한 정책이 존재합니다!");
            }
        }
        return "redirect:/admin/shipping-policies";
    }

    @DeleteMapping("/{shipping-policy-id}")
    public String deleteShippingPolicy(@PathVariable("shipping-policy-id") Long shippingPolicyId,
                                       RedirectAttributes redirectAttributes) {
        try {
            orderClient.deleteShippingPolicy(shippingPolicyId);
            redirectAttributes.addFlashAttribute("success", "배송 정책이 성공적으로 삭제되었습니다!");
        } catch (FeignException e) {
            redirectAttributes.addFlashAttribute("error", "배송 정책 삭제에 실패했습니다!");
        }
        return "redirect:/admin/shipping-policies";
    }


}
