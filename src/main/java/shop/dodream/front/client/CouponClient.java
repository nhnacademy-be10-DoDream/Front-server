package shop.dodream.front.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import shop.dodream.front.dto.AvailableCouponResponse;
import shop.dodream.front.dto.BookAvailableCouponResponse;

import java.util.List;

@FeignClient(name = "couponClient", url = "${gateway.url}")
public interface CouponClient {
	
	@GetMapping("/coupons")
	List<BookAvailableCouponResponse> getAvailableCoupons(@RequestParam Long bookId, @RequestParam Long salePrice);

	@GetMapping("/coupons/me/available")
	List<AvailableCouponResponse> getAvailableCoupons();

	@PutMapping("/coupons/me/{couponId}/apply")
	void putCouponStatus(@PathVariable("couponId") Long couponId);
	
	
}