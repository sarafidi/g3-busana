package com.busana.service.pattern;

public class StandardShipping implements ShippingStrategy {
    @Override
    public double calculateShippingFee() {
        return 5.00;
    }
}