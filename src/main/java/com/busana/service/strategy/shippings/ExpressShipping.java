package com.busana.service.strategy.shippings;

public class ExpressShipping implements ShippingStrategy {
    @Override
    public double calculateShippingFee() {
        return 15.00;
    }
}