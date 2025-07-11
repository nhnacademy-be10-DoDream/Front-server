package shop.dodream.front.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import shop.dodream.front.dto.PointPolicy;

import java.util.List;

@FeignClient(name = "pointPolicyClient", url = "${gateway.url}")
public interface PointPolicyClient {
    @GetMapping("/public/point-policies")
    List<PointPolicy> getPointPolicies();

    @PostMapping("/admin/point-policies")
    List<PointPolicy> createPointPolicy(@RequestBody PointPolicy pointPolicy);

    @PutMapping("/admin/point-policies/{point-policy-id}")
    void updatePointPolicy(@PathVariable("point-policy-id") long pointPolicyId,
                           @RequestBody PointPolicy pointPolicy);

    @DeleteMapping("/admin/point-policies/{point-policy-id}")
    void deletePointPolicy(@PathVariable("point-policy-id") long pointPolicyId);
}