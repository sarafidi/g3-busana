package com.busana.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Notification")       // must match SQL table name
public class Notification {
    
    @Id
    @Column(name = "notificationID", length = 20, nullable = false)
    private String notificationID;
    
    @ManyToOne
    @JoinColumn(name = "orderID", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "customerID", nullable = false)     
    private Customer customer;

    @Column(name = "message", length = 255, nullable = false)
    private String message;

    @Column(name = "status", length = 20, nullable = false)
    private String status = "unread";

    // --- Constructor --------------------------
    public Notification() {}        // always include an empty constructor for JPA

    // --- Getter & Setters --------------------------
    public String getNotificationID() { return notificationID; }
    public void setNotificationID(String notificationID) { 
        this.notificationID = notificationID; 
    }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getMessage() {  return message; }
    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) {
        this.status = status;
    }

}
