///Observer Interface
package com.busana.service.observer;

import com.busana.model.Order;

public interface OrderObserver {
    public void update(Order order);
}