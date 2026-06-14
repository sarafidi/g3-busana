package com.busana.service.pattern;

public class SameDayShipping implements ShippingStrategy {
    @Override
    public double calculateShippingFee(Order order) {
        return 30.00;
    }
}