// Concrete Subject
package com.busana.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.busana.model.Order;

import java.util.ArrayList;
@Component
public class OrderManager implements OrderSubject {
    
    private List <OrderObserver> observers = new ArrayList<>();
    //private String orderStatus;
    @Override
    public void addObserver(OrderObserver observer) {
        observers.add(observer);        
    }
    public OrderManager(List<OrderObserver> observers) {
        this.observers = observers;
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
