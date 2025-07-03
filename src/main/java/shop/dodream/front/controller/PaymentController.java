package shop.dodream.front.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import shop.dodream.front.client.PaymentClient;

import java.util.Map;

@Controller
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentClient paymentClient;
    @RequestMapping(method = RequestMethod.GET)
    public String checkOut() {
        return "order/payment/checkout";
    }

    @RequestMapping(value = "/confirm")
    @ResponseBody
    public ResponseEntity<Map<String ,Object>> confirmPayment(@RequestBody String jsonBody) {
        Map<String ,Object> response = paymentClient.confirm(jsonBody);
        int statusCode = response.containsKey("error") ? 400 : 200;

        return ResponseEntity.status(statusCode).body(response);
    }

    @GetMapping("/fail")
    public String failPayment(HttpServletRequest request, Model model) {
        model.addAttribute("errorCode", request.getParameter("code"));
        model.addAttribute("errorMessage", request.getParameter("message"));
        return "order/payment/fail";
    }

    @RequestMapping(value = "/success", method = RequestMethod.GET)
    public String successPayment(HttpServletRequest request, Model model) {
        model.addAttribute("code", request.getParameter("code"));
        model.addAttribute("message", request.getParameter("message"));

        return "order/payment/success";
    }
}
