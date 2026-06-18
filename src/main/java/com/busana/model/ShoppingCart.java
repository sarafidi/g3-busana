package com.busana.model;

import jakarta.persistence.*;

@Entity
@Table(name = "ShoppingCart")
public class ShoppingCart {
    @Id
    @Column(name = "cartID", length = 20, nullable = false)
    private String cartID;

    @ManyToOne
    @JoinColumn(name = "customerID", nullable = false)
    private Customer customer;

    public ShoppingCart() { }

    public ShoppingCart(String cartID, Customer customer) {
        this.cartID = cartID;
        this.customer = customer;
    }

    public String getCartID() { return cartID; }
    public void setCartID(String cartID) { this.cartID = cartID; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
}