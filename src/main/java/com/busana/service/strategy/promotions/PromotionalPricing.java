package com.busana.service.strategy.promotions;

public class PromotionalPricing implements PricingStrategy {
    private double discountValue;
    private String discountType;

    public PromotionalPricing(double discountValue, String discountType) {
        this.discountValue = discountValue;
        this.discountType = discountType;
    }

    // "fixed" / "flat"  -> subtract flat amount
    // "percentage"      -> multiply by remaining percentage
    @Override
    public double calculatePrice(double basePrice) {
        double finalPrice = (discountType.equalsIgnoreCase("fixed") || discountType.equalsIgnoreCase("flat"))
                ? basePrice - discountValue
                : (100 - discountValue)/100 * basePrice;
        return Math.max(finalPrice, 0);
    }
}