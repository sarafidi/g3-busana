package com.busana.model;

import java.math.BigDecimal;

import jakarta.persistence.*;
@Entity
@Table(name = "OrderItem")
public class OrderItem {
    
    @Id
    @Column(name = "orderItemID", length = 20, nullable = false)
    private String orderItemID;
    
    @ManyToOne
    @JoinColumn(name = "orderID", nullable = false)
    private Order order;
    
    @ManyToOne
    @JoinColumn(name = "variantID", nullable = false)
    private ProductVariant variant;

    @Column(name = "quantity", nullable = false)    
    private Integer quantity = 0; 

    @Column(name = "subtotal", precision = 10, scale = 2, nullable = false)     
    private BigDecimal subtotal = BigDecimal.ZERO;
    

    public OrderItem() {}        // always include an empty constructor for JPA

    public String getOrderItemID() {
        return orderItemID;
    }
    public void setOrderItemID(String orderItemID) {
        this.orderItemID = orderItemID;
    }

    public Order getOrder() {
        return order;
    }
    public void setOrder(Order order) {
        this.order = order;
    }


    public ProductVariant getVariant() {
        return variant;
    }
    public void setVariant(ProductVariant variant) {
        this.variant = variant;
    }


    public Integer getQuantity() {
        return quantity;
    }
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }


    public BigDecimal getsubTotal() {
        return subtotal;
    }
    public void getsubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

}