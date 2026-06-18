package com.busana.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller
class PreviewController {
    @GetMapping("/preview/cart")
    public String previewCart(Model model) {
        model.addAttribute("activePage", "cart");

        // fake data for preview
        model.addAttribute("cartItems", List.of(
                Map.of("productName", "Dress A", "VariantLabel", "Size M / Red", "quantity", 2, "subtotal", 120),
                Map.of("productName", "Dress B", "VariantLabel", "Size L / Blue", "quantity", 1, "subtotal", 89)
        ));

        return "customer/cart";
    }

}