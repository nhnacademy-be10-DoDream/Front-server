package shop.dodream.front.controller;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import shop.dodream.front.client.PointPolicyClient;
import shop.dodream.front.dto.GradeType;
import shop.dodream.front.dto.PointPolicy;
import shop.dodream.front.dto.PolicyType;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/point-policies")
public class AdminPointPolicyController {
    private final PointPolicyClient pointPolicyClient;

    @GetMapping
    public String getPointPolicy(Model model) {
        model.addAttribute("pointPolicies", pointPolicyClient.getPointPolicies());
        model.addAttribute("grades", GradeType.values());
        model.addAttribute("policyTypes", PolicyType.values());
        model.addAttribute("activeMenu", "point-policy");
        return "admin/pointPolicy/point-policy-list";
    }

    @PostMapping
    public String createPointPolicy(@ModelAttribute PointPolicy request,
                                    RedirectAttributes redirectAttributes) {
        try {

            pointPolicyClient.createPointPolicy(request.withRateFromPercentage());
            redirectAttributes.addFlashAttribute("success", "정책이 성공적으로 생성되었습니다!");
        } catch (FeignException e) {
            if (HttpStatus.CONFLICT.value() == e.status()) {
                redirectAttributes.addFlashAttribute("error", "이미 동일한 등급과 정책 유형의 정책이 존재합니다!");
            }
        }

        return "redirect:/admin/point-policies";
    }

    @PutMapping("/{point-policy-id}")
    public String updatePointPolicy(@PathVariable("point-policy-id") Long pointPolicyId,
                                    @ModelAttribute PointPolicy request,
                                    RedirectAttributes redirectAttributes) {
        try {
            pointPolicyClient.updatePointPolicy(pointPolicyId, request.withRateFromPercentage());
            redirectAttributes.addFlashAttribute("success", "정책이 성공적으로 수정되었습니다!");
        } catch (FeignException e) {
            if (HttpStatus.CONFLICT.value() == e.status()) {
                redirectAttributes.addFlashAttribute("error", "이미 동일한 등급과 정책 유형의 정책이 존재합니다!");
            }
        }

        return "redirect:/admin/point-policies";
    }

    @DeleteMapping("/{point-policy-id}")
    public String deletePointPolicy(@PathVariable("point-policy-id") Long pointPolicyId,
                                    RedirectAttributes redirectAttributes) {
        pointPolicyClient.deletePointPolicy(pointPolicyId);
        redirectAttributes.addFlashAttribute("success", "정책이 성공적으로 삭제되었습니다!");

        return "redirect:/admin/point-policies";
    }

}
