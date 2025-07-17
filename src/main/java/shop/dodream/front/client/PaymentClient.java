package shop.dodream.front.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

@FeignClient(name = "paymentClient", url = "${gateway.url}")
public interface PaymentClient {
    @PostMapping("/public/orders/confirm/payment")
    Map<String ,Object> confirm(@RequestBody String payment,
                                @RequestHeader(name = "X-USER-ID", required = false) String userId,
                                @RequestHeader(name = "X-PAYMENT-PROVIDER") String paymentProvider);
}
