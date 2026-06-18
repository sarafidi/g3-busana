package com.busana.service.pattern;

// Default pricing strategy - applied when no promotion is active
public class RegularPricing implements PricingStrategy{
    @Override
    public double calculatePrice(double basePrice) {
        return basePrice;
    }
}