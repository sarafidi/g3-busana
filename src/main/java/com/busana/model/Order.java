package com.busana.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.*;

@Entity
@Table(name = "`Order`")
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

    @Column(name = "orderDate", nullable = false)
    private LocalDate orderDate; 

    @Column(name = "orderStatus", length = 20, nullable = false)     
    private String orderStatus = "Pending";

    @Column(name = "shippingFee", precision = 10, scale = 2, nullable = false)     
    private BigDecimal shippingFee = BigDecimal.ZERO;
    @Column(name = "totalAmount", precision = 10, scale = 2, nullable = false)     
    private BigDecimal totalAmount = BigDecimal.ZERO;


    @Column(name = "deliveryAddress", length = 255)
    private String deliveryAddress;

    @Column(name = "paymentStatus", length = 20, nullable = false)     
    private String paymentStatus = "Pending";

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private java.util.List<OrderItem> orderItems;

    public Order() {}

    public java.util.List<OrderItem> getOrderItems() { return orderItems; }
    public void setOrderItems(java.util.List<OrderItem> orderItems) { this.orderItems = orderItems; }

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