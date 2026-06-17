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

import java.util.List;

@Controller
public class AuthController {

    @Autowired
    private AuthService authService;
    @Autowired
    private final ProductService productService;

    public AuthController(AuthService authService, ProductService productService) {
        this.authService = authService;
        this.productService = productService;
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
        return "customer/login";
    }

    @PostMapping("/customer/login")
    public String loginCustomer(@RequestParam String email, @RequestParam String password, HttpSession session, Model model) {
        Customer customer = authService.loginCustomer(email, password);
        if (customer != null) {
            session.setAttribute("customerID", customer.getCustomerID());
            session.setAttribute("customerName", customer.getName());
            return "redirect:/customer/home-temporary";
        }
        model.addAttribute("error", "Invalid email or password");
        return "redirect:/customer/login";
    }

    //customer logout --need fix later
    @GetMapping("/logout")
    public String logoutCustomer(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    // customer home
    @GetMapping("/customer/home-temporary")
    public String homeCustomer(HttpSession session, Model model) {
        String customerID = (String) session.getAttribute("customerID");
        if (customerID == null) return "redirect:/customer/login";

        model.addAttribute("customerName", session.getAttribute("customerName"));

        List<Product> featuredProducts = productService.getCustomerCatalogue(
                null, null, null, null, null, null
        );
        model.addAttribute("featuredProducts", featuredProducts);
        model.addAttribute("categories", productService.getCategories());
        return "customer/home-temporary";
    }

    //admin login
    @GetMapping("/admin/login") 
    public String showAdminLoginForm() {
        return "admin/login";
    }   

    @PostMapping("/admin/login")
    public String loginAdmin(@RequestParam String email, @RequestParam String password, HttpSession session, Model model) {
        Admin admin = authService.loginAdmin(email, password);
        if (admin != null) {
            session.setAttribute("admin", admin);
            return "redirect:/admin/dashboard";
        }
        model.addAttribute("error", "Invalid email or password");
        return "admin/login";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard() {
        return "admin/dashboard";
    }

    //admin logout
    @GetMapping("/admin/logout")    
    public String logoutAdmin(HttpSession session) {
        session.invalidate();
        return "redirect:/admin/login";
    }

}