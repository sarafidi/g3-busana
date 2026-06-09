package com.busana.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "ProductVariant")
public class ProductVariant {

    @Id
    @Column(name = "variantID", length = 20, nullable = false)
    private String variantID;

    @ManyToOne
    @JoinColumn(name = "productID", nullable = false)
    private Product product;

    @Column(name = "size", length = 5, nullable = false)
    private String size;

    @Column(name = "colour", length = 20, nullable = false)
    private String colour;

    @Column(name = "stockLevel", nullable = false)
    private int stockLevel;

    public ProductVariant() {}

    public String getVariantID() { return variantID; }
    public void setVariantID(String variantID) { this.variantID = variantID; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    public String getColour() { return colour; }
    public void setColour(String colour) { this.colour = colour; }

    public int getStockLevel() { return stockLevel; }
    public void setStockLevel(int stockLevel) { this.stockLevel = stockLevel; }

    @Transient
    public boolean isAvailable() {
        return stockLevel > 0;
    }

    @Transient
    public String getVariantLabel() {
        return colour + " / " + size;
    }
}
