package com.busana.service.observer;

import com.busana.model.Order;

public interface OrderSubject {

   public void addObserver(OrderObserver observer);
   public void removeObeserver(OrderObserver observe);
   public void notifyObservers(Order order);
} 