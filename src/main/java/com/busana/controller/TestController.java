package com.busana.controller;

import com.busana.model.*;
import com.busana.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class TestController {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;
    
    // visit http://localhost:8080/ to test this
    @GetMapping("/")
    public String home(Model model) {
        // pass data to HTML template
        model.addAttribute("pageTitle", "Welcome to BUSANA.my");
        model.addAttribute("successMessage", "SpringBoot + Thymeleaf + MySQL is working correctly!");

        // returns templates/customer/home.html
        return "customer/home";
    }
    
    @GetMapping("/test")
    public String test(@RequestParam Model model) {
        // visit http://localhost:8080/test to see a plain confirmation
        model.addAttribute("pageTitle", "Stack Test");
        model.addAttribute("successMessage", "Controller -> Service -> Repository chain is ready");
        return "customer/home";
    }

    @GetMapping("/db-check")
    @ResponseBody
    public String dbCheck() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== DB CHECK ===\n\n");

        sb.append("--- Customers ---\n");
        List<Customer> customers = customerRepository.findAll();
        for (Customer c : customers) {
            sb.append(String.format("ID: %s | Name: %s | Email: %s\n", c.getCustomerID(), c.getName(), c.getEmail()));
        }

        sb.append("\n--- Shopping Carts ---\n");
        List<ShoppingCart> carts = shoppingCartRepository.findAll();
        for (ShoppingCart cart : carts) {
            sb.append(String.format("CartID: %s | CustomerID: %s\n", cart.getCartID(), cart.getCustomer() != null ? cart.getCustomer().getCustomerID() : "null"));
        }

        sb.append("\n--- Cart Items ---\n");
        List<CartItem> items = cartItemRepository.findAll();
        for (CartItem item : items) {
            sb.append(String.format("ItemID: %s | CartID: %s | VariantID: %s | Qty: %d | Subtotal: %d\n", 
                item.getCartItemID(), 
                item.getCart() != null ? item.getCart().getCartID() : "null",
                item.getVariant() != null ? item.getVariant().getVariantID() : "null",
                item.getQuantity(),
                item.getSubtotal()));
        }

        return "<pre>" + sb.toString() + "</pre>";
    }
}
