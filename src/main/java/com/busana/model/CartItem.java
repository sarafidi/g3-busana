package com.busana.model;

import jakarta.persistence.*;

@Entity
@Table(name = "CartItem")
public class CartItem {
    @Id
    @Column (name = "cartItemID", length = 20, nullable = false)
    private String cartItemID;
    
    @ManyToOne
    @JoinColumn (name = "cartID", nullable = false)
    private ShoppingCart cart;
    
    @ManyToOne
    @JoinColumn (name = "variantID", nullable = false)
    private ProductVariant variant;
    
    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "subtotal", nullable = false)
    private int subtotal;

    public CartItem() { }

    public CartItem(String cartItemID, ShoppingCart cart, ProductVariant variant, int quantity, int subtotal) {
        this.cartItemID = cartItemID;
        this.cart = cart;
        this.variant = variant;
        this.quantity = quantity;this.subtotal = subtotal; }

    public String getCartItemID() { return cartItemID; }
    public void setCartItemID(String cartItemID) { this.cartItemID = cartItemID; }

    public ShoppingCart getCart() { return cart; }
    public void setCart(ShoppingCart cart) { this.cart = cart; }

    public ProductVariant getVariant() { return variant; }
    public void setVariant(ProductVariant variant) { this.variant = variant; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public int getSubtotal() { return subtotal; }
    public void setSubtotal(int subtotal) { this.subtotal = subtotal; }
}