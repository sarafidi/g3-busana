package com.busana.controller;


import com.busana.model.Admin;
import com.busana.model.Customer;
import com.busana.model.Product;
import com.busana.service.AuthService;
import com.busana.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.math.BigDecimal;
import java.util.Comparator;
import com.busana.model.Order;
import com.busana.model.Promotion;
import com.busana.service.OrderService;
import com.busana.service.PromotionService;

@Controller
public class AuthController {

    private final AuthService authService;
    private final ProductService productService;
    private final OrderService orderService;
    private final PromotionService promotionService;

    public AuthController(AuthService authService, ProductService productService, OrderService orderService, PromotionService promotionService) {
        this.authService = authService;
        this.productService = productService;
        this.orderService = orderService;
        this.promotionService = promotionService;
    }

    //customer registration
    @GetMapping("/customer/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("customer", new Customer());
        return "customer/register";
    }

    @PostMapping("/customer/register")
    public String registerCustomer(@ModelAttribute Customer customer, Model model) {
        String result = authService.registerCustomer(customer);
        model.addAttribute("message", result);
        return "customer/register";
    }   

    //customer login
    @GetMapping("/customer/login")
    public String redirectToCustomerLogin() {
        return "redirect:/";
    }

    @PostMapping("/customer/login")
    public String loginCustomer(@RequestParam String email, @RequestParam String password, HttpSession session, RedirectAttributes redirectAttributes) {
        Customer customer = authService.loginCustomer(email, password);
        if (customer != null) {
            session.setAttribute("customerID", customer.getCustomerID());
            session.setAttribute("customerName", customer.getName());
            return "redirect:/customer/home";
        }
        redirectAttributes.addFlashAttribute("error", "Invalid email or password");
        redirectAttributes.addFlashAttribute("activeTab", "customer");
        return "redirect:/";
    }

    //customer logout --need fix later
    @GetMapping("/logout")
    public String logoutCustomer(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    // customer home
    @GetMapping("/customer/home")
    public String homeCustomer(HttpSession session, Model model) {
        String customerID = (String) session.getAttribute("customerID");
        if (customerID == null) return "redirect:/customer/login";

        model.addAttribute("customerName", session.getAttribute("customerName"));

        List<Product> featuredProducts = productService.getCustomerCatalogue(
                null, null, null, null, null, null
        );
        model.addAttribute("featuredProducts", featuredProducts);
        model.addAttribute("categories", productService.getCategories());
        return "customer/home";
    }

    //admin login
    @GetMapping("/admin/login") 
    public String showAdminLoginForm() {
        return "admin/login";
    }   

    @PostMapping("/admin/login")
    public String loginAdmin(
            @RequestParam String email, 
            @RequestParam String password, 
            @RequestHeader(value = "Referer", required = false) String referer,
            HttpSession session, 
            RedirectAttributes redirectAttributes) {
        Admin admin = authService.loginAdmin(email, password);
        if (admin != null) {
            session.setAttribute("admin", admin);
            return "redirect:/admin/dashboard";
        }
        redirectAttributes.addFlashAttribute("error", "Invalid email or password");
        if (referer != null && referer.contains("/admin/login")) {
            return "redirect:/admin/login";
        }
        redirectAttributes.addFlashAttribute("activeTab", "admin");
        return "redirect:/";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(HttpSession session, Model model) {
        if (session.getAttribute("admin") == null) {
            return "redirect:/admin/login";
        }
        
        List<Product> allProducts = productService.getAdminCatalogue(null, null, null);
        long totalProducts = allProducts.size();
        
        // Count low stock (any variant stock < 5)
        long lowStockCount = allProducts.stream()
            .filter(p -> p.getVariants().stream().anyMatch(v -> v.getStockLevel() < 5))
            .count();
            
        List<Order> allOrders = orderService.getAllOrders();
        long totalOrders = allOrders.size();
        
        long pendingOrdersCount = allOrders.stream()
            .filter(o -> "Pending".equalsIgnoreCase(o.getOrderStatus()))
            .count();
            
        BigDecimal totalRevenue = allOrders.stream()
            .filter(o -> "Completed".equalsIgnoreCase(o.getOrderStatus()) || "Shipped".equalsIgnoreCase(o.getOrderStatus()) || "Paid".equalsIgnoreCase(o.getOrderStatus()))
            .map(Order::getTotalAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
            
        long totalPromotions = promotionService.getAllPromotions().size();
        
        // Pass top 5 recent orders sorted by date descending
        List<Order> recentOrders = allOrders.stream()
            .sorted(Comparator.comparing(Order::getOrderDate).reversed())
            .limit(5)
            .toList();
            
        // Pass low stock items (top 5)
        List<Product> lowStockItems = allProducts.stream()
            .filter(p -> p.getVariants().stream().anyMatch(v -> v.getStockLevel() < 5))
            .limit(5)
            .toList();

        model.addAttribute("pageTitle", "Admin Dashboard");
        model.addAttribute("adminName", ((Admin) session.getAttribute("admin")).getName());
        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("lowStockCount", lowStockCount);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("pendingOrdersCount", pendingOrdersCount);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("totalPromotions", totalPromotions);
        model.addAttribute("recentOrders", recentOrders);
        model.addAttribute("lowStockItems", lowStockItems);
        
        return "admin/dashboard";
    }

    //admin logout
    @GetMapping("/admin/logout")    
    public String logoutAdmin(HttpSession session) {
        session.invalidate();
        return "redirect:/admin/login";
    }

}