package com.busana.controller;

import com.busana.model.*;
import com.busana.service.CartWishlistService;
import com.busana.service.CartWishlistService.CheckoutResult;
import com.busana.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class CartWishlistController {
    @Autowired
    private CartWishlistService cartWishlistService;
    
    @Autowired
    private OrderService orderService;

    @GetMapping({"/cart", "/customer/cart"})
    public String viewCart(HttpSession session, Model model) {
        String customerID = getCustomerID(session);
        if (customerID == null) return "redirect:/customer/login";

        List<CartItem> cartItems = cartWishlistService.viewCart(customerID);
        double cartTotal = cartItems.stream().mapToDouble(CartItem::getSubtotal).sum();

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("cartTotal", cartTotal);
        return "customer/cart";
    }

    @PostMapping("/cart/add")
    public String addToCart(
            HttpSession session,
            @RequestParam String variantID,
            @RequestParam int quantity,
            RedirectAttributes redirectAttributes
    ) {
        String customerID = getCustomerID(session);
        if (customerID == null) return "redirect:/customer/login";

        try {
            cartWishlistService.addToCart(customerID, variantID, quantity);
            return "redirect:/cart";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/cart";
        }
    }

    @PostMapping("/cart/update")
    public String updateCartItem(
            HttpSession session,
            @RequestParam String cartItemID,
            @RequestParam int quantity,
            RedirectAttributes redirectAttributes
    ) {
        String customerID = getCustomerID(session);
        if (customerID == null) return "redirect:/customer/login";

        try {
            cartWishlistService.updateCartItem(customerID, cartItemID, quantity);
            return "redirect:/cart";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/cart";
        }
    }

    @PostMapping("/cart/remove")
    public String removeFromCart(
            HttpSession session,
            @RequestParam String cartItemID,
            RedirectAttributes redirectAttributes
    ) {
        String customerID = getCustomerID(session);
        if (customerID == null) return "redirect:/customer/login";

        try {
            cartWishlistService.removeFromCart(customerID, cartItemID);
            return "redirect:/cart";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/cart";
        }
    }

    @GetMapping("/wishlist")
    public String viewWishlist(HttpSession session, Model model) {
        String customerID = getCustomerID(session);
        if (customerID == null) return "redirect:/customer/login";

        List<WishlistItem> wishlistItems = cartWishlistService.viewWishlist(customerID);
        model.addAttribute("wishlistItems", wishlistItems);
        return "customer/wishlist";
    }

    @PostMapping("/wishlist/add")
    public String addToWishlist(
            HttpSession session,
            @RequestParam String variantID,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes
    ) {
        String customerID = getCustomerID(session);
        if (customerID == null) return "redirect:/customer/login";

        try {
            cartWishlistService.addToWishlist(customerID, variantID);
            redirectAttributes.addFlashAttribute("successMessage", "Item added to wishlist successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        String referer = request.getHeader("Referer");
        return referer != null ? "redirect:" + referer : "redirect:/customer/products";
    }

    @PostMapping("/wishlist/toggle")
    public String toggleWishlist(
            HttpSession session,
            @RequestParam String variantID,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes
    ) {
        String customerID = getCustomerID(session);
        if (customerID == null) return "redirect:/customer/login";

        try {
            boolean removed = cartWishlistService.toggleWishlist(customerID, variantID);
            if (removed) {
                redirectAttributes.addFlashAttribute("successMessage", "Item removed from wishlist successfully!");
            } else {
                redirectAttributes.addFlashAttribute("successMessage", "Item added to wishlist successfully!");
            }
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        String referer = request.getHeader("Referer");
        return referer != null ? "redirect:" + referer : "redirect:/customer/products";
    }

    @PostMapping("/wishlist/remove")
    public String removeFromWishlist(
            HttpSession session,
            @RequestParam String wishlistItemID,
            RedirectAttributes redirectAttributes
    ) {
        String customerID = getCustomerID(session);
        if (customerID == null) return "redirect:/customer/login";

        try {
            cartWishlistService.removeFromWishlist(customerID, wishlistItemID);
            return "redirect:/wishlist";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/wishlist";
        }
    }

    @GetMapping({"/checkout", "/customer/checkout"})
    public String viewCheckout(
            HttpSession session,
            @RequestParam(value = "shippingMethod", required = false, defaultValue = "standard") String shippingMethod,
            Model model
    ) {
        String customerID = getCustomerID(session);
        if (customerID == null) return "redirect:/customer/login";

        List<CartItem> cartItems = cartWishlistService.viewCart(customerID);
        if (cartItems.isEmpty()) return "redirect:/cart";

        CheckoutResult checkoutResult = cartWishlistService.calculateCheckout(cartItems, shippingMethod);

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("subtotal", checkoutResult.subtotal());
        model.addAttribute("shippingMethod", shippingMethod);
        model.addAttribute("shippingFee", checkoutResult.shippingFee());
        model.addAttribute("totalAmount", checkoutResult.totalAmount());
        model.addAttribute("totalDisplay", "RM " + String.format("%.2f", checkoutResult.totalAmount()) + 
                           " (Subtotal: RM " + String.format("%.2f", checkoutResult.subtotal()) + 
                           " + Shipping: RM " + String.format("%.2f", checkoutResult.shippingFee()) + ")");
        
        return "customer/checkout";
    }

    @PostMapping({"/checkout", "/customer/checkout"})
    public String confirmOrder(
            HttpSession session,
            @RequestParam String shippingMethod,
            @RequestParam String deliveryAddress,
            RedirectAttributes redirectAttributes
    ) {
        String customerID = getCustomerID(session);
        if (customerID == null) return "redirect:/customer/login";

        try {
            List<CartItem> cartItems = cartWishlistService.viewCart(customerID);
            if (cartItems.isEmpty()) return "redirect:/cart";

            CheckoutResult checkoutResult = cartWishlistService.calculateCheckout(cartItems, shippingMethod);

            Order order = orderService.placeOrder(customerID, cartItems, checkoutResult.shippingFee(), checkoutResult.totalAmount(), deliveryAddress);

            cartWishlistService.clearCart(customerID);

            return "redirect:/customer/order-confirmation/" + order.getOrderID();
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/checkout";
        }
    }

    @GetMapping("/order/confirm")
    public String orderConfirm(HttpSession session) {
        String customerID = getCustomerID(session);
        if (customerID == null) return "redirect:/customer/login";
        return "customer/order-confirmation";
    }

    private String getCustomerID(HttpSession session) {
        return (String) session.getAttribute("customerID");
    }
}