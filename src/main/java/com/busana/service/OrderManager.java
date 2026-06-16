// Concrete Subject
package com.busana.service;

import java.util.List;

import com.busana.model.Order;

import java.util.ArrayList;
public class OrderManager implements OrderSubject {
    
    private List <OrderObserver> observers = new ArrayList<>();
    //private String orderStatus;
    @Override
    public void addObserver(OrderObserver observer) {
        observers.add(observer);        
    }
    @Override
    public void removeObeserver(OrderObserver observer) {
        observers.remove(observer);
    }
    @Override
    public void notifyObservers(Order order) {
        for(OrderObserver observer: observers)
            observer.update(order);
    }

    // public void updateOrderStatus(Order order, String newStatus){
    //     this.orderStatus = newStatus;
    //     notifyObservers(order);
    // }

    
}
