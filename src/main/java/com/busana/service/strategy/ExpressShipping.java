package com.busana.service.strategy;

public class ExpressShipping implements ShippingStrategy {
    @Override
    public double calculateShippingFee() {
        return 15.00;
    }
}