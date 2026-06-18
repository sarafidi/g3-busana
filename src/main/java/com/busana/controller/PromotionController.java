package com.busana.controller;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import com.busana.model.Promotion;
import com.busana.service.PromotionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping
public class PromotionController {
    
        private final PromotionService promotionService;

        public PromotionController(PromotionService promotionService) {
            this.promotionService = promotionService;
        }

    @GetMapping("/admin/promotions")
    public String viewAllPromotions(Model model) {
        model.addAttribute("pageTitle", "Promotions");
        model.addAttribute("promotions", promotionService.getAllPromotions());
        return "admin/promotion-list";
    }

    @GetMapping("/admin/promotions/new")
    public String showCreatePromotionsForm(Model model) {
        Promotion emptyPromotion = new Promotion();

        model.addAttribute("pageTitle", "Create A New Promotion");
        model.addAttribute("promotion", emptyPromotion);

        return "admin/promotion-form";
    }

     @GetMapping("/admin/promotions/{promotionId}/edit")
    public String showEditProductForm(
        @PathVariable String promotionId,
        // @RequestParam(required = false) String focus,
        Model model,
        RedirectAttributes redirectAttributes
    ) {
        try {
            Promotion existingPromotion = promotionService.getPromotionByID(promotionId);
            model.addAttribute("promotion", existingPromotion);
            model.addAttribute("pageTitle", "Edit Promotion");
            // model.addAttribute("focusSection", focus);
            return "admin/promotion-form";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/admin/promotions";
        }
    }

    @PostMapping("/admin/promotions")

    public String savePromotion(
        @ModelAttribute("promotion") Promotion promotion,
        Model model,
        RedirectAttributes redirectAttributes
    ) {
        
        try {
           promotionService.createPromotion(promotion);
           redirectAttributes.addFlashAttribute("successMessage", "Promotion " + promotion.getPromotionName() + " was saved successfully.");
                   return "redirect:/admin/promotions";

        } catch (IllegalArgumentException ex) {
          model.addAttribute("pageTitle", "Create A New Promotion");
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("promotion", promotion);
            return "admin/promotion-form";
        }
    }


    @PostMapping("/admin/promotions/{promotionId}")
    public String updatePromotion(
        @PathVariable String promotionId,
        @ModelAttribute("promotion") Promotion updatedPromotion,
        Model model,
        RedirectAttributes redirectAttributes
     ) {
        try {
            promotionService.updatePromotion(promotionId, updatedPromotion);
            redirectAttributes.addFlashAttribute("successMessage", "Promotion " + updatedPromotion.getPromotionName() + " was updated successfully.");
            return "redirect:/admin/promotions";
        } catch (IllegalArgumentException ex) {
model.addAttribute("pageTitle", "Edit Promotion");
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("promotion", updatedPromotion);           
             return "admin/promotion-form";
        }
    }

    @PostMapping("/admin/promotions/{promotionId}/delete")
    public String deletePromotion(@PathVariable String promotionId, RedirectAttributes redirectAttributes) {
        try {
            Promotion promotion = promotionService.getPromotionByID(promotionId);
            promotionService.deletePromotion(promotionId);
            redirectAttributes.addFlashAttribute("successMessage", "Promotion " + promotion.getPromotionName() + " was deleted successfully.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/promotions";
    }

}
