package shop.dodream.front.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import shop.dodream.front.dto.OrderResponse;
import shop.dodream.front.dto.ShippingPolicyDto;

import java.util.List;

@FeignClient(name = "orderClient", url = "http://localhost:10320")
public interface OrderClient {
//    @PostMapping("/orders")
//    Map<String, Object> createOrder(@RequestBody OrderRequest orderRequest);
//
//    @GetMapping("/orders/gift-wrap")
//    List<WrappingDto> getGiftWraps();

//    @GetMapping("/orders/gift-wrap/{gift-wrap-id}")
//    WrappingDto getGiftWrapById(@PathVariable("gift-wrap-id") Long giftWrapId);

    @GetMapping("/orders/shipping")
    List<ShippingPolicyDto> getShippingPolicies(@CookieValue("accessToken")String accessToken);
    // Assuming you want to fetch shipping policies as well

    @GetMapping("/orders")
    List<OrderResponse> getOrders(@CookieValue("accessToken") String accessToken); // 주문 목록 조회

//    @GetMapping("/orders/{order-id}")
//    OrderDetailsDto getOrderDetail(@PathVariable("order-id") String orderId); // 주문 상세 조회
//
//    @PostMapping("/orders/{order-id}/cancel")
//    Map<String, Object> cancelOrder(@PathVariable("order-id") String orderId, PaymentCancelRequest request); // 주문 취소
//
//    @PostMapping("/orders/{order-id}/refund")
//    Map<String, Object> returnOrder(@PathVariable("order-id") String orderId, PaymentCancelRequest request); //환불
}
