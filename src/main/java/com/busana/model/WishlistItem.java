package com.busana.model;

import jakarta.persistence.*;

@Entity
@Table(name = "WishlistItem")
public class WishlistItem {
    @Id
    @Column (name = "wishlistItemID", length = 20, nullable = false)
    private String wishlistItemID;

    @ManyToOne
    @JoinColumn (name = "wishlistID", nullable = false)
    private Wishlist wishlist;

    @ManyToOne
    @JoinColumn (name = "variantID", nullable = false)
    private ProductVariant variant;

    public WishlistItem() { }

    public WishlistItem(String wishlistItemID, Wishlist wishlist, ProductVariant variant) {
        this.wishlistItemID = wishlistItemID;
        this.wishlist = wishlist;
        this.variant = variant;
    }

    public String getWishlistItemID() { return wishlistItemID; }
    public void setWishlistItemID(String wishlistItemID) { this.wishlistItemID = wishlistItemID; }

    public Wishlist getWishlist() { return wishlist; }
    public void setWishlist(Wishlist wishlist) { this.wishlist = wishlist; }

    public ProductVariant getVariant() { return variant; }
    public void setVariant(ProductVariant variant) { this.variant = variant; }

}