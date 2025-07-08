package shop.dodream.front.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "paymentClient", url = "${gateway.url}")
public interface PaymentClient {
    @PostMapping("/public/orders/confirm/payment")
    Map<String ,Object> confirm(@RequestBody String payment);
}
