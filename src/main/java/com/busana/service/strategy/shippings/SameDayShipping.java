package com.busana.service.strategy.shippings;

public class SameDayShipping implements ShippingStrategy {
    @Override
    public double calculateShippingFee() {
        return 30.00;
    }
}