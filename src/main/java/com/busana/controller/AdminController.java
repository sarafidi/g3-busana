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
}