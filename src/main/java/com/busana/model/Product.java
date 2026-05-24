package com.busana.model;

import java.math.BigDecimal;

import jakarta.persistence.*;

@Entity
@Table(name = "Product")
public class Product {
    
    @Id
    @Column(name = "productID", length = 20, nullable = false)
    private String productID;
    
    @ManyToOne
    @JoinColumn(name = "categoryID", nullable = false)
    private Category categoryID;
    
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

    // --- Constructor --------------------------
    public Product() {}

    // --- Getter & Setters --------------------------

    public String getProductID() { return productID; }
    public void setProductID(String productID) { this.productID = productID; }

    public Category getCategoryID() { return categoryID; }
    public void setCategoryID(Category categoryID) { this.categoryID = categoryID; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getBasePrice() { return basePrice; }
    public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }

    public String getFabricType() { return fabricType; }
    public void setFabricType(String fabricType) { this.fabricType = fabricType; }

    public String getImages() { return images; }
    public void setImages(String images) { this.images = images; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }    
}
