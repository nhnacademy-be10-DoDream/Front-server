package shop.dodream.front.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import shop.dodream.front.client.OrderClient;
import shop.dodream.front.client.UserClient;
import shop.dodream.front.dto.CartItemResponse;
import shop.dodream.front.dto.OrderRequest;

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
    @GetMapping
    public String orderSheet(Model model, HttpServletRequest request) {
        List<CartItemResponse> cartItems = List.of(
                new CartItemResponse(1L, 1L, "호날두 되는법" , 120000L, 2L, "http://storage.java21.net:8000/dodream-images/book/0524d09f-0fb9-4671-8611-ef6e00983628.png"),
                new CartItemResponse(2L, 2L, "호날두 자서전" , 50000L, 2L, "http://storage.java21.net:8000/dodream-images/book/0524d09f-0fb9-4671-8611-ef6e00983628.png")
        );
        try {
            model.addAttribute("addressList", userClient.getAddresses());
        } catch (Exception e) {
            // 예외가 발생하면 빈 리스트로 설정
            model.addAttribute("addressList", Collections.emptyList());
        }

        model.addAttribute("shippingPolicies", orderClient.getShippingPolicies());

        // 장바구니에서 선택된 아이템을 주문서로 전달
        model.addAttribute("orderItems", cartItems);
        // 총 금액 계산
        int totalAmount = 0;
        totalAmount += cartItems.stream()
                .mapToInt(cartItem -> Math.toIntExact(cartItem.getSalePrice() * cartItem.getQuantity()))
                .sum();
        model.addAttribute("totalAmount", totalAmount);
//        model.addAttribute("userId", "user");

        return "order/order-sheet"; // 주문서 페이지로 이동
    }

    @PostMapping("/payment")
    public String pay(@ModelAttribute OrderRequest orderRequest,
                      @RequestParam("wantedDateRaw") String wantedDateRaw,
                      HttpServletRequest request) {
        ZonedDateTime zoned = LocalDate.parse(wantedDateRaw)
                .atStartOfDay(ZoneId.of("Asia/Seoul"));
        orderRequest.setWantedDate(zoned);
        orderRequest.setUserId("user");
        //주문서 저장
        Map<String, Object> orderResponse = orderClient.createOrder(orderRequest);
        HttpSession session = request.getSession();
        session.setAttribute("orderResponse", orderResponse);
        //결제창 리다이렉트
        return "redirect:/payment?orderId=%s&totalPrice=%s"
                .formatted(orderResponse.get("orderId"), orderResponse.get("totalPrice"));
    }
}
