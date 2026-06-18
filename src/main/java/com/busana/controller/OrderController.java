package com.busana.controller;

import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import org.springframework.ui.Model;

import com.busana.model.Order;
import com.busana.service.OrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping
public class OrderController {
            private final OrderService orderService;

            public OrderController(OrderService orderService) {
                this.orderService = orderService;
            }
    @GetMapping("/admin/orders")
    public String viewAllOrders(Model model) {
        model.addAttribute("pageTitle", "Orders");
        model.addAttribute("orders", orderService.getAllOrders());
        return "admin/order-list";
    }

    @GetMapping("/admin/orders/{orderId}")
    public String viewOrderDetails(
        @PathVariable String orderId,
        Model model,
        RedirectAttributes redirectAttributes
    ) {
        try {
            Order order = orderService.getOrderByOrderID(orderId);
            model.addAttribute("pageTitle", order.getOrderID());
            model.addAttribute("order", order);
            return "admin/order-detail";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/admin/orders";
        }
    }

    @GetMapping("/customer/order-confirmation/{orderId}")
    public String viewOrderConfirmationDetails(
        @PathVariable String orderId,
        Model model,
        RedirectAttributes redirectAttributes
    ) {
        try {
            Order order = orderService.getOrderByOrderID(orderId);
            model.addAttribute("pageTitle", "Order Confirmation for Order: " + order.getOrderID());
            model.addAttribute("order", order);
            
            model.addAttribute("deliveryAddress", order.getDeliveryAddress());
            model.addAttribute("shippingFee", order.getShippingFee());
            model.addAttribute("totalAmount", order.getTotalAmount());
            model.addAttribute("subtotal", order.getTotalAmount().subtract(order.getShippingFee()));
            
            // Deduce shipping method from shipping fee
            String method = "Standard";
            if (order.getShippingFee().compareTo(BigDecimal.valueOf(15.00)) == 0) {
                method = "Express";
            } else if (order.getShippingFee().compareTo(BigDecimal.valueOf(30.00)) == 0) {
                method = "Same-day Courier";
            }
            model.addAttribute("shippingMethod", method);
            
            return "customer/order-confirmation";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/customer/checkout";
        }
    }

    // Admin Updates Order Status
    @PostMapping("/admin/orders/{orderId}")
    public String updateOrderStatus(
        @PathVariable String orderId,
        // @PathVariable String newStatus,
        @RequestParam String newStatus,
        RedirectAttributes redirectAttributes
    ) {
        try {
            orderService.updateOrderStatus(orderId, newStatus);
            redirectAttributes.addFlashAttribute("successMessage", "Order status updated to: " + newStatus);
            return "redirect:/admin/orders/" + orderId;
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/admin/orders/" + orderId;
        }
    }

}