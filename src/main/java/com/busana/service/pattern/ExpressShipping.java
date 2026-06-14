package com.busana.service.pattern;

public class ExpressShipping implements ShippingStrategy {
    @Override
    public double calculateShippingFee(Order order) {
        return 15.00;
    }
}