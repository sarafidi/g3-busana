package com.busana.model;

import jakarta.persistence.*;

@Entity
@Table(name = "WishlistItem")
public class WishlistItem {
    @Id
    @Column (name = "wishlistItemID", length = 20, nullable = false)
    private String wishlistItemID;

    @ManyToOne
    @JoinColumn (name = "variantID", nullable = false)
    private ProductVariant variant;

    public WishlistItem() { }

    public WishlistItem(String wishlistItemID, ProductVariant variant) {
        this.wishlistItemID = wishlistItemID;
        this.variant = variant;
    }

    public String getWishlistItemID() { return wishlistItemID; }
    public void setWishlistItemID(String wishlistItemID) { this.wishlistItemID = wishlistItemID; }

    public ProductVariant getVariant() { return variant; }
    public void setVariant(ProductVariant variant) { this.variant = variant; }
}