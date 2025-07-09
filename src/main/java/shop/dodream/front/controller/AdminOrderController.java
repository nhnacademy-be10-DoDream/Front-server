package shop.dodream.front.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import shop.dodream.front.client.OrderClient;
import shop.dodream.front.dto.OrderByStatusResponse;

@Controller
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderClient orderClient;

    @GetMapping
    public String getOrdersByStatus(@RequestParam(value = "status", required = false, defaultValue = "WAITING") String status,
                                    @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                    @RequestParam(value = "size", required = false, defaultValue = "10") int size,
                                    Model model) {
        // 주문 목록 조회
        Page<OrderByStatusResponse> orders = orderClient.getOrdersByStatus(status, page, size);
        model.addAttribute("orders", orders);
        model.addAttribute("activeMenu", "adminOrders");
        return "admin/order/orders";
    }

    @PostMapping("/order-status/waiting")
    public String processOrderWaiting(@RequestParam String orderId) {
        orderClient.processOrderWaiting(orderId);
        return "redirect:/admin/orders";
    }
}
