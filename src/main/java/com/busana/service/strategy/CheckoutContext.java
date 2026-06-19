package com.busana.service.strategy;

import org.springframework.stereotype.Service;

@Service
public class CheckoutContext {
    private ShippingStrategy shippingStrategy;
    private PricingStrategy pricingStrategy;

    public void setShippingStrategy(ShippingStrategy shippingStrategy) { this.shippingStrategy = shippingStrategy; }

    public void setPricingStrategy(PricingStrategy pricingStrategy) { this.pricingStrategy = pricingStrategy; }

    public double executeShipping() {
        return shippingStrategy.calculateShippingFee();
    }

    public double executePrice(double basePrice) {
        return pricingStrategy.calculatePrice(basePrice);
    }
}