package com.busana.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.*;

@Entity
@Table(name = "`Order`")   //prevents SQL conflicts with the "ORDER BY" statement     // must match SQL table name
public class Order {
    
    @Id
    @Column(name = "orderID", length = 20, nullable = false)
    private String orderID;
    
    @ManyToOne
    @JoinColumn(name = "customerID", nullable = false)
    private Customer customer;
    
    @ManyToOne
    @JoinColumn(name = "promotionID")
    private Promotion promotion;

    @Column(name = "orderDate", nullable = false)     // order should have date AND time imo
    private LocalDate orderDate; 

    @Column(name = "orderStatus", length = 20, nullable = false)     
    private String orderStatus = "Pending";

    @Column(name = "shippingFee", precision = 10, scale = 2, nullable = false)     
    private BigDecimal shippingFee = BigDecimal.ZERO; //avoiding object instantiation
    @Column(name = "totalAmount", precision = 10, scale = 2, nullable = false)     
    private BigDecimal totalAmount = BigDecimal.ZERO;


    @Column(name = "deliveryAddress", length = 255)     // nullable = true by default
    private String deliveryAddress;

    @Column(name = "paymentStatus", length = 20, nullable = false)     
    private String paymentStatus = "Pending";

    // --- Constructor --------------------------
    public Order() {}        // always include an empty constructor for JPA

    // --- Getter & Setters --------------------------

    public String getOrderID() { return orderID; }
    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public Customer getCustomer() {  return customer;}
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Promotion getPromotion() { return promotion; }
    public void setPromotion(Promotion promotion) {
        this.promotion = promotion;
    }

    public LocalDate getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderStatus() { return orderStatus;}
    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public BigDecimal getShippingFee() { return shippingFee; }
    public void setShippingFee(BigDecimal shippingFee) {
        this.shippingFee = shippingFee;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }
    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }
    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

}
