package com.busana.controller;

import com.busana.model.CartItem;
import com.busana.model.ShoppingCart;
import com.busana.model.Wishlist;
import com.busana.model.WishlistItem;
import com.busana.service.CartWishlistService;
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
@RequestMapping("/")
public class CartWishlistController {
    @Autowired
    private CartWishlistService cartWishlistService;

    @GetMapping("/cart")
    public String viewCart(HttpSession session, Model model) {
        String customerID = getCustomerID(session);
        if (customerID == null) return "redirect:/login";

        List<CartItem> cartItems = cartWishlistService.viewCart(customerID);
        model.addAttribute("cartItems", cartItems);
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
        if (customerID == null) return "redirect:/login";

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
        if (customerID == null) return "redirect:/login";

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
        if (customerID == null) return "redirect:/login";

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
        if (customerID == null) return "redirect:/login";

        List<WishlistItem> wishlistItems = cartWishlistService.viewWishlist(customerID);
        model.addAttribute("wishlistItems", wishlistItems);
        return "customer/wishlist";
    }

    @PostMapping("/wishlist/add")
    public String addToWishlist(
            HttpSession session,
            @RequestParam String variantID,
            RedirectAttributes redirectAttributes
    ) {
        String customerID = getCustomerID(session);
        if (customerID == null) return "redirect:/login";

        try {
            cartWishlistService.addToWishlist(customerID, variantID);
            return "redirect:/wishlist";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/wishlist";
        }
    }

    @PostMapping("/wishlist/remove")
    public String removeFromWishlist(
            HttpSession session,
            @RequestParam String wishlistItemID,
            RedirectAttributes redirectAttributes
    ) {
        String customerID = getCustomerID(session);
        if (customerID == null) return "redirect:/login";

        try {
            cartWishlistService.removeFromWishlist(customerID, wishlistItemID);
            return "redirect:/wishlist";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/wishlist";
        }
    }

    private String getCustomerID(HttpSession session) {
        return (String) session.getAttribute("customerID");
    }

}