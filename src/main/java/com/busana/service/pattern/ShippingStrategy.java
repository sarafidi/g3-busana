package com.busana.service.pattern;

public interface ShippingStrategy {
    double calculateShippingFee(Order order);
}