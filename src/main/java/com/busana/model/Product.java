package com.busana.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.persistence.*;

@Entity
@Table(name = "Product")
public class Product {
    
    @Id
    @Column(name = "productID", length = 20, nullable = false)
    private String productID;
    
    @ManyToOne
    @JoinColumn(name = "categoryID", nullable = false)
    private Category category;
    
    @Column(name = "name", length = 100, nullable = false)
    private String name;
    
    @Column(name = "description", length = 255)
    private String description;
    
    @Column(name = "basePrice", nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;
    
    @Column(name = "fabricType", length = 100, nullable = false)
    private String fabricType;
    
    @Column(name = "images", length = 255)
    private String images;
    
    @Column(name = "status", length = 20, nullable = false)
    private String status;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("colour ASC, size ASC")
    private List<ProductVariant> variants = new ArrayList<>();

    // --- Constructor --------------------------
    public Product() {}

    // --- Getter & Setters --------------------------

    public String getProductID() { return productID; }
    public void setProductID(String productID) { this.productID = productID; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getBasePrice() { return basePrice; }
    public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }

    public String getFabricType() { return fabricType; }
    public void setFabricType(String fabricType) { this.fabricType = fabricType; }

    public String getImages() { return images; }
    public void setImages(String images) {
        this.images = images;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<ProductVariant> getVariants() { return variants; }

    public void setVariants(List<ProductVariant> variants) {
        this.variants.clear();
        if (variants == null) {
            return;
        }

        for (ProductVariant variant : variants) {
            addVariant(variant);
        }
    }

    public void addVariant(ProductVariant variant) {
        if (variant == null) {
            return;
        }
        variant.setProduct(this);
        this.variants.add(variant);
    }

    @Transient
    public String getCategoryName() {
        return category != null ? category.getCategoryName() : "";
    }

    @Transient
    public int getTotalStock() {
        return variants.stream().mapToInt(ProductVariant::getStockLevel).sum();
    }

    @Transient
    public boolean isInStock() {
        return getTotalStock() > 0;
    }

    @Transient
    public String getPrimaryImage() {
        if (images == null || images.isBlank()) {
            return "";
        }

        String[] imagePaths = images.split(",");
        return imagePaths.length == 0 ? "" : imagePaths[0].trim();
    }

    @Transient
    public List<String> getImageList() {
        if (images == null || images.isBlank()) {
            return List.of();
        }

        List<String> imageList = new ArrayList<>();
        for (String image : images.split(",")) {
            if (!image.isBlank()) {
                imageList.add(image.trim());
            }
        }
        return imageList;
    }

    @Transient
    public List<String> getAvailableSizes() {
        Set<String> sizes = new LinkedHashSet<>();
        for (ProductVariant variant : variants) {
            if (variant.getSize() != null && !variant.getSize().isBlank()) {
                sizes.add(variant.getSize().trim());
            }
        }
        return new ArrayList<>(sizes);
    }

    @Transient
    public List<String> getAvailableColours() {
        Set<String> colours = new LinkedHashSet<>();
        for (ProductVariant variant : variants) {
            if (variant.getColour() != null && !variant.getColour().isBlank()) {
                colours.add(variant.getColour().trim());
            }
        }
        return new ArrayList<>(colours);
    }

    @Transient
    public String getVariantSummary() {
        if (variants.isEmpty()) {
            return "No variants";
        }

        return variants.stream()
            .map(variant -> variant.getColour() + " / " + variant.getSize())
            .collect(Collectors.joining(", "));
    }

    @Transient
    public String getAvailableSizesText() {
        return String.join(", ", getAvailableSizes());
    }

    @Transient
    public String getAvailableColoursText() {
        return String.join(", ", getAvailableColours());
    }
}