package com.busana.service.strategy.shippings;

public class StandardShipping implements ShippingStrategy {
    @Override
    public double calculateShippingFee() {
        return 5.00;
    }
}