package shop.dodream.front.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import shop.dodream.front.client.OrderClient;
import shop.dodream.front.client.UserClient;
import shop.dodream.front.dto.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderClient orderClient;
    private final UserClient userClient;

    @PostMapping
    public String orderSheet(Model model, @ModelAttribute("items") CartOrderRequest cartOrderRequest) {
        List<OrderItemRequest> cartItems = cartOrderRequest.getItems();
        cartItems.forEach(item -> {
            if (item.getWrappingId() != null) {
                item.setWrappingInfo(orderClient.getGiftWrapById(item.getWrappingId()));
            }
        });

        try {
            model.addAttribute("addressList", userClient.getAddresses());
        } catch (Exception e) {
            model.addAttribute("addressList", Collections.emptyList());
        }

        model.addAttribute("shippingPolicies", orderClient.getShippingPolicies());

        model.addAttribute("orderItems", cartItems);

        model.addAttribute("totalAmount", cartOrderRequest.getOrderTotal());
            if (cartOrderRequest.getUserId() != null) {
            model.addAttribute("availablePoint", userClient.getAvailablePoint(cartOrderRequest.getUserId()));
        }


        return "order/order-sheet"; // 주문서 페이지로 이동
    }

    @PostMapping("/payment")
    public String pay(@ModelAttribute OrderRequest orderRequest,
                      @RequestParam("wantedDateRaw") String wantedDateRaw,
                      @RequestParam(value = "userId", required = false) String userId) {
        ZonedDateTime zoned = LocalDate.parse(wantedDateRaw)
                .atStartOfDay(ZoneId.of("Asia/Seoul"));
        orderRequest.setWantedDate(zoned);


        Map<String, Object> orderResponse;
        if (userId == null || userId.isEmpty()) {
            // 비회원 주문 처리
            orderResponse = orderClient.createOrderForGuest(orderRequest);
        } else {
            // 회원 주문 처리
            orderRequest.setUserId(userId);
            orderResponse = orderClient.createOrder(orderRequest);
        }

        //결제창 리다이렉트
        return "redirect:/payment?orderId=%s&totalPrice=%s"
                .formatted(orderResponse.get("orderId"), orderResponse.get("totalPrice"));
    }

    @GetMapping("detail/{order-id}")
    public String getOrderDetail(@PathVariable("order-id") String orderId, Model model) {
        // 주문 상세 조회
        OrderDetailsDto orderDetail = orderClient.getOrderDetail(orderId);
        model.addAttribute("order", orderDetail);
        return "order/order-detail"; // 주문 상세 페이지로 이동

    }

    @GetMapping("/{orderId}/cancel-sheet")
    public String showCancelForm(@PathVariable String orderId,
                                 @RequestParam String type,
                                 Model model) {
        // 주문 취소 폼 표시
        model.addAttribute("orderId", orderId);
        model.addAttribute("type", type);
        return "order/cancel-sheet";
    }

    @PostMapping("/{orderId}/cancel")
    public String cancelOrder(@PathVariable String orderId,
                              PaymentCancelRequest request,
                              @RequestParam String type) {
        Map<String, Object> response;
        if ("cancel".equals(type)) {
            response = orderClient.cancelOrder(orderId, request);
        } else if ("refund".equals(type)) {
            response = orderClient.returnOrder(orderId, request);
        }

        return "redirect:/order/detail/%s".formatted(orderId);
    }

    @PostMapping("/guest")
    public String getGuestOrders(Model model, GuestAuthRequest guestAuthRequest) {
        model.addAttribute("orders", orderClient.getOrdersForGuest(guestAuthRequest));
        return "order/guest-orders"; // 비회원 주문 목록 페이지로 이동
    }
}
