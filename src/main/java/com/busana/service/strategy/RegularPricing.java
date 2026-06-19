package com.busana.service.strategy;

public class RegularPricing implements PricingStrategy{
    @Override
    public double calculatePrice(double basePrice) {
        return basePrice;
    }
}