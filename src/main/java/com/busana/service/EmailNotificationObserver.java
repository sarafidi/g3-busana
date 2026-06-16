//Concrete Observer
package com.busana.service;

import org.springframework.stereotype.Component;

import com.busana.model.Order;

@Component
public class EmailNotificationObserver implements OrderObserver {
    // private String customerEmail;
    @Override
    public void update(Order order) {
      sendEmail(order.getCustomer().getEmail(), order.getOrderStatus());  
    }

    // public EmailNotificationObserver(String customerEmail) {
    //     this.customerEmail = customerEmail;
    // }

    public void sendEmail(String customerEmail, String orderStatus){
        System.out.println(customerEmail + "has been emailed about the order status change to:" + orderStatus );
    }
}
