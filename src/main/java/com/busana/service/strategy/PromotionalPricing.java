package com.busana.service.strategy;

public class PromotionalPricing implements PricingStrategy {
    private double discountValue;
    private String discountType;

    public PromotionalPricing(double discountValue, String discountType) {
        this.discountValue = discountValue;
        this.discountType = discountType;
    }

    // "fixed"          -> subtract flat amount
    // "percentage"     -> multiply by remaining percentage
    @Override
    public double calculatePrice(double basePrice) {
        double finalPrice =  discountType.equals("fixed")
                ? basePrice - discountValue
                : (100 - discountValue)/100 * basePrice;
        return Math.max(finalPrice, 0);
    }
}