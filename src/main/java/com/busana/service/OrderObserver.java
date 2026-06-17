///Observer Interface
package com.busana.service;

import com.busana.model.Order;

public interface OrderObserver {
    public void update(Order order);
}
