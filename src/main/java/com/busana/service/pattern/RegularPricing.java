package com.busana.service.pattern;

public class RegularPricing implements PricingStrategy{
    @Override
    public double calculatePrice(double basePrice) {
        return basePrice;
    }
}