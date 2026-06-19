package com.busana.service.factory;

import com.busana.model.Category;
import com.busana.model.Product;
import com.busana.model.ProductVariant;
import com.busana.service.ProductService;

public interface ProductFactory {
    String getSupportedCategory();

    Product createProduct(ProductService.ProductFormData formData, Category category, String productId);

    void updateProduct(Product product, ProductService.ProductFormData formData, Category category);

    ProductVariant createVariant(Product product, ProductService.ProductVariantFormData variantFormData, String variantId);
}