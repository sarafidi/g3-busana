package com.busana.service;


import java.util.List;
import java.util.Comparator;
import java.math.BigDecimal;
import java.time.LocalDate;

import com.busana.service.observer.EmailNotificationObserver;
import com.busana.service.observer.OrderManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.busana.model.Order;
import com.busana.model.OrderItem;
import com.busana.model.Customer;
import com.busana.model.ProductVariant;
import com.busana.model.CartItem;
import com.busana.repository.OrderRepository;
import com.busana.repository.CustomerRepository;
import com.busana.repository.OrderItemRepository;
import com.busana.repository.ProductVariantRepository;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderManager orderManager;
    private final CustomerRepository customerRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductVariantRepository productVariantRepository;

    public OrderService(
            OrderRepository orderRepository, 
            OrderManager orderManager, 
            EmailNotificationObserver emailObserver,
            CustomerRepository customerRepository,
            OrderItemRepository orderItemRepository,
            ProductVariantRepository productVariantRepository
    ) {
            this.orderRepository = orderRepository;
            this.orderManager = orderManager;
            this.customerRepository = customerRepository;
            this.orderItemRepository = orderItemRepository;
            this.productVariantRepository = productVariantRepository;
    }

    @Transactional
    public Order placeOrder(String customerID, List<CartItem> cartItems, double shippingFee, double totalAmount, String deliveryAddress) {
        Customer customer = customerRepository.findById(customerID.trim())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + customerID));

        String orderId = "ORD-" + System.currentTimeMillis();

        Order order = new Order();
        order.setOrderID(orderId);
        order.setCustomer(customer);
        order.setOrderDate(LocalDate.now());
        order.setOrderStatus("Pending");
        order.setShippingFee(BigDecimal.valueOf(shippingFee));
        order.setTotalAmount(BigDecimal.valueOf(totalAmount));
        order.setDeliveryAddress(deliveryAddress);
        order.setPaymentStatus("Paid");

        Order savedOrder = orderRepository.save(order);

        for (int i = 0; i < cartItems.size(); i++) {
            CartItem cartItem = cartItems.get(i);
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderItemID("OI-" + System.currentTimeMillis() + "-" + i);
            orderItem.setOrder(savedOrder);
            orderItem.setVariant(cartItem.getVariant());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setSubtotal(BigDecimal.valueOf(cartItem.getSubtotal()));
            orderItemRepository.save(orderItem);

            // Decrement variant stock
            ProductVariant variant = cartItem.getVariant();
            int newStock = variant.getStockLevel() - cartItem.getQuantity();
            if (newStock < 0) {
                throw new IllegalStateException("Not enough stock for variant: " + variant.getVariantID());
            }
            variant.setStockLevel(newStock);
            productVariantRepository.save(variant);
        }

        return savedOrder;
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
       return orderRepository.findById(orderID.trim()).orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderID));
       
    }

    //Admin updating status
    @Transactional //all or nothing operation
    public void updateOrderStatus(String orderID, String newStatus) {
        Order order = getOrderByOrderID(orderID);
        order.setOrderStatus(newStatus);
        orderManager.notifyObservers(order);
        orderRepository.save(order);
    }

public int getTotalOrderCount() {
    List<?> orders = orderRepository.findAll();
    return orders.size();
}

public int getOrderCountByStatus(String status) {
    List<Order> orders = orderRepository.findAll();

    return (int) orders.stream()
        .filter(order -> order.getOrderStatus() != null && order.getOrderStatus().equalsIgnoreCase(status))
        .count();
}
}