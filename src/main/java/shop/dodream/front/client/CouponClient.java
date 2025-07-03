package shop.dodream.front.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import shop.dodream.front.dto.CouponResponse;

import java.util.List;

@FeignClient(name = "coupon", url = "http://localhost:10320")
public interface CouponClient {
	
	@GetMapping("/coupons")
	List<CouponResponse> getAvailableCoupons(@RequestParam Long bookId, @RequestParam Long salePrice);
	
	@PutMapping("/coupons/me/{couponId}/apply")
	void putCouponStatus(@PathVariable("couponId") Long couponId);
	
	
}