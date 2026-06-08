package com.busana.service;

import org.springframework.stereotype.Component;

import com.busana.model.Category;
import com.busana.model.Product;
import com.busana.model.ProductVariant;

@Component
public class AccessoriesFactory implements ProductFactory {

    @Override
    public String getSupportedCategory() {
        return "accessories";
    }

    @Override
    public Product createProduct(ProductService.ProductFormData formData, Category category, String productId) {
        Product product = new Product();
        product.setProductID(productId);
        applyFields(product, formData, category);
        return product;
    }

    @Override
    public void updateProduct(Product product, ProductService.ProductFormData formData, Category category) {
        applyFields(product, formData, category);
    }

    @Override
    public ProductVariant createVariant(Product product, ProductService.ProductVariantFormData variantFormData, String variantId) {
        ProductVariant variant = new ProductVariant();
        variant.setVariantID(variantId);
        variant.setProduct(product);
        variant.setSize(variantFormData.getSize());
        variant.setColour(variantFormData.getColour());
        variant.setStockLevel(variantFormData.getStockLevel());
        return variant;
    }

    private void applyFields(Product product, ProductService.ProductFormData formData, Category category) {
        ensureCategory(category);
        product.setCategory(category);
        product.setName(formData.getName());
        product.setDescription(formData.getDescription());
        product.setBasePrice(formData.getBasePrice());
        product.setFabricType(formData.getFabricType());
        product.setImages(formData.getImages());
        product.setStatus(formData.getStatus());
    }

    private void ensureCategory(Category category) {
        if (category == null || !getSupportedCategory().equalsIgnoreCase(category.getCategoryName())) {
            throw new IllegalArgumentException("AccessoriesFactory can only create products for the Accessories category.");
        }
    }
}
