package com.busana.service.pattern;

// flat rate - order parameter reserved for future weight-based calculation
// weight column currently not in DB scope
public class StandardShipping implements ShippingStrategy {
    @Override
    public double calculateShippingFee() {
        return 5.00;
    }
}