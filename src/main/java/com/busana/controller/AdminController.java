package com.busana.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.busana.service.OrderService;
import com.busana.service.PromotionService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final PromotionService promotionService;
    private final OrderService orderService;

    public AdminController(PromotionService promotionService, OrderService orderService) {
        this.promotionService = promotionService;
        this.orderService = orderService;
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        model.addAttribute("pageTitle", "Executive Dashboard");

        // 1. Fetch Order Status Segment Matrices
        model.addAttribute("totalOrders", orderService.getTotalOrderCount());
        model.addAttribute("pendingOrders", orderService.getOrderCountByStatus("PENDING"));
        model.addAttribute("processingOrders", orderService.getOrderCountByStatus("PROCESSING"));
        model.addAttribute("deliveredOrders", orderService.getOrderCountByStatus("DELIVERED"));
        model.addAttribute("cancelledOrders", orderService.getOrderCountByStatus("CANCELLED"));

        // 2. Fetch Promotion Status Segment Matrices
        model.addAttribute("totalPromotions", promotionService.getTotalPromotionsCount());
        model.addAttribute("activePromotions", promotionService.getPromotionCountByStatus("ACTIVE"));
        model.addAttribute("inactivePromotions", promotionService.getPromotionCountByStatus("INACTIVE"));

        return "admin/dashboard";
    }
}