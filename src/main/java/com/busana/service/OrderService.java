package com.busana.service;


import java.util.List;
import java.util.Comparator;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.busana.model.Order;
import com.busana.repository.OrderRepository;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderManager orderManager;
    //private final EmailNotificationObserver emailObserver;

    //Constructor 
    public OrderService(OrderRepository orderRepository, OrderManager orderManager, EmailNotificationObserver emailObserver) {
            this.orderRepository = orderRepository;
            this.orderManager = orderManager;
            //this.emailObserver = emailObserver;
          // orderManager.addObserver(emailObserver);
    }

    // View Orders by Admin
         
    //Admins view all orders w/o any filtering
    public List<Order> getAllOrders() {
        List<Order> orders;
            orders = orderRepository.findAll();
        

        return orders.stream()
            .sorted(Comparator.comparing(Order::getOrderDate))
            .toList();
    }

    // Admins view all orders under a specific customer using their ID
        public List<Order> getOrdersByCustomerID(String customerID) {
        List<Order> orders;
        
            orders = orderRepository.findByCustomer_CustomerID(customerID.trim());
       
        return orders.stream()
            .sorted(Comparator.comparing(Order::getOrderDate))
            .toList();
    }

    // View orders by ID
    public Order getOrderByOrderID(String orderID) {
       return orderRepository.findById(orderID.trim()).orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderID)); //trim removes whitespace
       
    }

    //Admin updating status
    @Transactional //all or nothing operation
    public Order updateOrderStatus(String orderID, String newStatus) {
        Order order = getOrderByOrderID(orderID);
        order.setOrderStatus(newStatus);
        orderManager.notifyObservers(order);
        return orderRepository.save(order);
    }

}