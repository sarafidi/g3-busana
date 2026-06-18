package com.busana.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import com.busana.model.Product;
import com.busana.service.ProductService;
import com.busana.service.ProductService.ProductFormData;

@Controller
@RequestMapping
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/customer/products")
    public String viewCustomerCatalogue(
        @RequestParam(required = false) String search,
        @RequestParam(required = false) String categoryId,
        @RequestParam(required = false) String size,
        @RequestParam(required = false) String colour,
        @RequestParam(required = false) BigDecimal minPrice,
        @RequestParam(required = false) BigDecimal maxPrice,
        Model model
    ) {
        List<Product> products = productService.getCustomerCatalogue(search, categoryId, size, colour, minPrice, maxPrice);

        model.addAttribute("pageTitle", "Product Catalogue");
        model.addAttribute("products", products);
        model.addAttribute("categories", productService.getCategories());
        model.addAttribute("sizes", productService.getAvailableSizes());
        model.addAttribute("colours", productService.getAvailableColours());
        model.addAttribute("search", search);
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("selectedSize", size);
        model.addAttribute("selectedColour", colour);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute(
            "emptyMessage",
            "No products matched your filters. Try a different keyword, category, size, colour, or price range."
        );
        return "customer/product-list";
    }

    @GetMapping("/customer/products/{productId}")
    public String viewCustomerProductDetail(
        @PathVariable String productId,
        Model model,
        RedirectAttributes redirectAttributes
    ) {
        try {
            Product product = productService.getCustomerProduct(productId);
            model.addAttribute("pageTitle", product.getName());
            model.addAttribute("product", product);
            model.addAttribute("relatedProducts", productService.getCustomerCatalogue(null, product.getCategory().getCategoryID(), null, null, null, null)
                .stream()
                .filter(item -> !item.getProductID().equals(productId))
                .limit(3)
                .toList());
            return "customer/product-detail";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/customer/products";
        }
    }

    @GetMapping("/admin/products")
    public String viewAdminCatalogue(
        @RequestParam(required = false) String categoryId,
        @RequestParam(required = false) String stockFilter,
        @RequestParam(required = false) String statusFilter,
        Model model
    ) {
        model.addAttribute("pageTitle", "Catalogue Manager");
        model.addAttribute("products", productService.getAdminCatalogue(categoryId, stockFilter, statusFilter));
        model.addAttribute("categories", productService.getCategories());
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("selectedStockFilter", stockFilter);
        model.addAttribute("selectedStatusFilter", statusFilter);
        return "admin/product-list";
    }

    @GetMapping("/admin/products/new")
    public String showCreateProductForm(Model model) {
        ProductFormData productFormData = productService.createEmptyForm();
        populateFormModel(model, productFormData, "create", null);
        return "admin/product-form";
    }

    @GetMapping("/admin/products/{productId}/edit")
    public String showEditProductForm(
        @PathVariable String productId,
        @RequestParam(required = false) String focus,
        Model model,
        RedirectAttributes redirectAttributes
    ) {
        try {
            Product product = productService.getProduct(productId);
            ProductFormData productFormData = productService.buildForm(productId);
            populateFormModel(model, productFormData, "edit", productId);
            model.addAttribute("product", product);
            model.addAttribute("focusSection", focus);
            return "admin/product-form";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/admin/products";
        }
    }

    @PostMapping("/admin/products")
    public String createProduct(
        @ModelAttribute("productForm") ProductFormData productFormData,
        BindingResult bindingResult,
        Model model,
        RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            populateFormModel(model, productFormData, "create", null);
            model.addAttribute("errorMessage", "Please review the form values and try again.");
            return "admin/product-form";
        }

        try {
            Product product = productService.createProduct(productFormData);
            redirectAttributes.addFlashAttribute("successMessage", "Product " + product.getName() + " was created successfully.");
            return "redirect:/admin/products";
        } catch (IllegalArgumentException ex) {
            populateFormModel(model, productFormData, "create", null);
            model.addAttribute("errorMessage", ex.getMessage());
            return "admin/product-form";
        }
    }

    @PostMapping("/admin/products/{productId}")
    public String updateProduct(
        @PathVariable String productId,
        @ModelAttribute("productForm") ProductFormData productFormData,
        BindingResult bindingResult,
        Model model,
        RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            populateFormModel(model, productFormData, "edit", productId);
            model.addAttribute("errorMessage", "Please review the form values and try again.");
            return "admin/product-form";
        }

        try {
            Product product = productService.updateProduct(productId, productFormData);
            redirectAttributes.addFlashAttribute("successMessage", "Product " + product.getName() + " was updated successfully.");
            return "redirect:/admin/products";
        } catch (IllegalArgumentException ex) {
            populateFormModel(model, productFormData, "edit", productId);
            model.addAttribute("errorMessage", ex.getMessage());
            return "admin/product-form";
        }
    }

    @PostMapping("/admin/products/{productId}/delete")
    public String deleteProduct(@PathVariable String productId, RedirectAttributes redirectAttributes) {
        try {
            Product product = productService.getProduct(productId);
            productService.deleteProduct(productId);
            redirectAttributes.addFlashAttribute("successMessage", "Product " + product.getName() + " was deleted successfully.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/products";
    }

    private void populateFormModel(Model model, ProductFormData productFormData, String formMode, String productId) {
        productFormData.ensureAtLeastOneVariant();
        model.addAttribute("pageTitle", "create".equals(formMode) ? "Add Product" : "Edit Product");
        model.addAttribute("formMode", formMode);
        model.addAttribute("productId", productId);
        model.addAttribute("formAction", productId == null ? "/admin/products" : "/admin/products/" + productId);
        model.addAttribute("productForm", productFormData);
        model.addAttribute("categories", productService.getCategories());
    }
}
