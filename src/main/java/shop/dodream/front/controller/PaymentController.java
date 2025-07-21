package shop.dodream.front.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import shop.dodream.front.client.CartClient;
import shop.dodream.front.client.PaymentClient;
import shop.dodream.front.dto.CartResponse;
import shop.dodream.front.dto.PaymentResultDto;

import java.util.Arrays;
import java.util.Map;

@Controller
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentClient paymentClient;
    private final CartClient cartClient;
    private final CartController cartController;

    @GetMapping
    public String checkOut() {
        return "order/payment/checkout";
    }

    @PostMapping("/confirm")
    public ResponseEntity<Map<String ,Object>> confirmPayment(@RequestBody String jsonBody,HttpServletRequest request,
                                                              @RequestHeader(value = "X-USER-ID", required = false) String userId,
                                                              @RequestHeader(value = "X-PAYMENT-PROVIDER", required = false) String paymentProvider) {
        Map<String ,Object> response = paymentClient.confirm(jsonBody, userId, paymentProvider);
        int statusCode = response.containsKey("error") ? 400 : 200;
        String guestId = cartController.getGuestIdFromCookie(request);
        if(guestId == null){
            CartResponse cart = cartClient.getCart();
            cartClient.deleteCart(cart.getCartId());
        }
        else {
            cartClient.deleteGuestCart(guestId);
        }
        return ResponseEntity.status(statusCode).body(response);
    }

    @GetMapping("process")
    public String processingPayment() {
        return "order/payment/processing";
    }

    @GetMapping("/fail")
    public String failPayment(HttpServletRequest request, Model model) {
        model.addAttribute("errorCode", request.getParameter("code"));
        model.addAttribute("errorMessage", request.getParameter("message"));
        return "order/payment/fail";
    }

    @GetMapping("/success")
    public String successPayment(@ModelAttribute PaymentResultDto result, Model model) {
        model.addAttribute("orderId", result.getOrderId());
        model.addAttribute("amount", result.getAmount());
        model.addAttribute("method", result.getMethod());
        model.addAttribute("address", result.getAddress());
        model.addAttribute("items", Arrays.asList(result.getItems().split(";")));
        return "order/payment/success";
    }
}
