package com.busana.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Wishlist")
public class Wishlist {
    @Id
    @Column(name = "wishlistID", length = 20, nullable = false)
    private String wishlistID;

    @ManyToOne
    @JoinColumn (name = "customerID", nullable = false)
    private Customer customer;

    public Wishlist() { }

    public Wishlist(String wishlistID, Customer customer) {
        this.wishlistID = wishlistID;
        this.customer = customer;
    }

    public String getWishlistID() { return wishlistID; }
    public void setWishlistID(String wishlistID) { this.wishlistID = wishlistID; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
}