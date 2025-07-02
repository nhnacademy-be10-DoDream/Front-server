package shop.dodream.front.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import shop.dodream.front.dto.CouponResponse;

import java.util.List;

@FeignClient(name = "coupon")
public interface CouponClient {
	
	@GetMapping("/coupon")
	List<CouponResponse> getAvailableCoupons(@RequestParam Long bookId, @RequestParam Long salePrice);
}