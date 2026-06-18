package com.busana.service.pattern;

public class ExpressShipping implements ShippingStrategy {
    @Override
    public double calculateShippingFee() {
        return 15.00;
    }
}