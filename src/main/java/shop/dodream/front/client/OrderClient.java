package shop.dodream.front.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import shop.dodream.front.dto.*;

import java.util.List;
import java.util.Map;

//@FeignClient(name = "orderClient", url = "${gateway.url}")
@FeignClient(name = "orderClient", url = "http://localhost:10327")
public interface OrderClient {
    @PostMapping("/orders")
    Map<String, Object> createOrder(@RequestBody OrderRequest orderRequest);
//
    @GetMapping("/public/orders/gift-wrap")
    List<WrappingDto> getGiftWraps();

    @GetMapping("/public/orders/gift-wrap/{gift-wrap-id}")
    WrappingDto getGiftWrapById(@PathVariable("gift-wrap-id") Long giftWrapId);

    @GetMapping("/public/orders/shipping")
    List<ShippingPolicyDto> getShippingPolicies();

    @PostMapping("/admin/orders/shipping-policy")
    ShippingPolicyDto createShippingPolicy(@RequestBody ShippingPolicyDto shippingPolicyDto);

    @PutMapping("/admin/orders/shipping-policy/{shipping-policy-id}")
    void updateShippingPolicy(@PathVariable("shipping-policy-id") Long shippingPolicyId,
                              @RequestBody ShippingPolicyDto shippingPolicyDto);

    @DeleteMapping("/admin/orders/shipping-policy/{shipping-policy-id}")
    void deleteShippingPolicy(@PathVariable("shipping-policy-id") Long shippingPolicyId);

    @GetMapping("/orders")
    Page<OrderResponse> getOrders(@RequestParam int page, @RequestParam int size); // 주문 목록 조회

    @GetMapping("/public/orders/{order-id}")
    OrderDetailsDto getOrderDetail(@PathVariable("order-id") String orderId); // 주문 상세 조회

    @PostMapping("/public/orders/{order-id}/cancel")
    Map<String, Object> cancelOrder(@PathVariable("order-id") String orderId, PaymentCancelRequest request); // 주문 취소

    @PostMapping("/orders/{order-id}/refund")
    Map<String, Object> returnOrder(@PathVariable("order-id") String orderId, PaymentCancelRequest request); //환불

    @PostMapping("/public/orders")
    Map<String, Object> createOrderForGuest(OrderRequest orderRequest); // 비회원 주문 생성

    @PostMapping("/public/orders/guest")
    Page<OrderResponse> getOrdersForGuest(@RequestBody GuestAuthRequest guestAuthRequest, @RequestParam int page, @RequestParam int size); // 비회원 주문 목록 조회

    @GetMapping("/admin/orders/order-status/{order-status}")
    Page<OrderByStatusResponse> getOrdersByStatus(@PathVariable("order-status") String status, @RequestParam int page, @RequestParam int size);

    @PostMapping("/admin/orders/{order-id}/delivery")
    void processOrderWaiting(@PathVariable("order-id") String orderId);


}
