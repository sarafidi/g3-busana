package com.busana.service.observer;

import java.util.List;

import org.springframework.stereotype.Component;

import com.busana.model.Order;

import java.util.ArrayList;
@Component
public class OrderManager implements OrderSubject {
    
    private List <OrderObserver> observers = new ArrayList<>();
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
}