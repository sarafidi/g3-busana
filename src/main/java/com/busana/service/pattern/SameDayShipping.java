package com.busana.service.pattern;

public class SameDayShipping implements ShippingStrategy {
    @Override
    public double calculateShippingFee() {
        return 30.00;
    }
}