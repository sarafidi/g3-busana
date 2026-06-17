package com.busana.controller;


import com.busana.model.Admin;
import com.busana.model.Customer;
import com.busana.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    @Autowired
    private AuthService authService;

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
            session.setAttribute("customer", customer);
            return "index";
        }
        model.addAttribute("error", "Invalid email or password");
        return "customer/login";
    }

    //customer logout --need fix later
    @GetMapping("/logout")
    public String logoutCustomer(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
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
