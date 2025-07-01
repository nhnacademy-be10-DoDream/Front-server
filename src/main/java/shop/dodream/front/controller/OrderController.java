package shop.dodream.front.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import shop.dodream.front.client.OrderClient;
import shop.dodream.front.client.UserClient;
import shop.dodream.front.dto.CartItem;
import shop.dodream.front.dto.UserAddressResponse;

import java.util.List;

@Controller
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderClient orderClient;
    private final UserClient userClient;
    @PostMapping()
    public String orderSheet(Model model, HttpServletRequest request) {
        List<CartItem> cartItems = List.of(
                new CartItem(1L, "호날두 되는법","http://storage.java21.net:8000/dodream-images/book/0524d09f-0fb9-4671-8611-ef6e00983628.png" , 120000, 100000, 2),
                new CartItem(2L, "호날두 자서전","http://storage.java21.net:8000/dodream-images/book/0524d09f-0fb9-4671-8611-ef6e00983628.png" , 50000, 40000, 1)
        );

        Cookie[] cookies = request.getCookies();
        String accessToken;
        for (Cookie cookie : cookies) {
            if ("accessToken".equals(cookie.getName())) {
                accessToken = cookie.getValue();
                // 사용자 주소 목록 가져오기
                List<UserAddressResponse> addressList = userClient.getAddresses(accessToken);
                model.addAttribute("addressList", addressList);
                break;
            }
        }
        // 장바구니에서 선택된 아이템을 주문서로 전달
        model.addAttribute("orderItems", cartItems);
        // 총 금액 계산
        int totalAmount = 0;
        totalAmount += cartItems.stream()
                .mapToInt(CartItem::getTotalPrice)
                .sum();
        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("shippingPolicies", orderClient.getShippingPolicies()); // 배송 정책 정보 가져오기


        return "order/order-sheet"; // 주문서 페이지로 이동
    }
}
