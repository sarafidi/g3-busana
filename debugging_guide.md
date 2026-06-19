# Debugging Guide: Strategy Pattern in Checkout Flow

This guide describes where to place breakpoints and what to inspect to verify the correct behavior and design of the **Strategy Pattern** implemented in the checkout flow of the Busana application.

The Strategy Pattern is used in `CartWishlistService.java` to dynamically calculate **Shipping Fees** (`ShippingStrategy`) and **Discounts/Pricing** (`PricingStrategy`).

---

## Class Structure Overview
- **Context**: `CheckoutContext.java`
- **Shipping Strategy Interface**: `ShippingStrategy.java`
  - *Concrete Strategies*: `StandardShipping.java`, `ExpressShipping.java`, `SameDayShipping.java`
- **Pricing Strategy Interface**: `PricingStrategy.java`
  - *Concrete Strategies*: `RegularPricing.java`, `PromotionalPricing.java`
- **Client**: `CartWishlistService.java` (specifically the `calculateCheckout` method)

---

## Breakpoints to Set

### 📌 Breakpoint 1: Shipping Strategy Selection
* **File**: [CartWishlistService.java](file:///c:/Users/saraf/Downloads/[CSE6234%20SOFT.%20DESIGN]/assignment/busana/src/main/java/com/busana/service/CartWishlistService.java#L286-L294)
* **Location**: Lines 286–294 (inside `calculateCheckout` method)
* **Code Segment**:
  ```java
  ShippingStrategy shippingStrategy;
  if ("express".equalsIgnoreCase(shippingMethod)) {
      shippingStrategy = new ExpressShipping();
  } else if ("sameday".equalsIgnoreCase(shippingMethod)) {
      shippingStrategy = new SameDayShipping();
  } else {
      shippingStrategy = new StandardShipping();
  }
  ```

#### What to inspect and say:
> **Explanation**: "This breakpoint verifies the dynamic selection of the concrete shipping strategy. Here, the system intercepts the client's choice of `shippingMethod` and instantiates the correct subclass (`StandardShipping`, `ExpressShipping`, or `SameDayShipping`)."
> * **Variables to watch**:
>   * `shippingMethod` (String): Verify it matches the user selection (e.g., `"standard"`, `"express"`, or `"sameday"`).
>   * `shippingStrategy` (ShippingStrategy): Confirm the correct concrete instance has been created (e.g., `ExpressShipping`).

---

### 📌 Breakpoint 2: Injecting Strategy into Context
* **File**: [CartWishlistService.java](file:///c:/Users/saraf/Downloads/[CSE6234%20SOFT.%20DESIGN]/assignment/busana/src/main/java/com/busana/service/CartWishlistService.java#L295)
* **Location**: Line 295
* **Code Segment**:
  ```java
  checkoutContext.setShippingStrategy(shippingStrategy);
  ```

#### What to inspect and say:
> **Explanation**: "This breakpoint shows strategy injection. The client sets the concrete strategy object inside `CheckoutContext`, delegating subsequent math operations to this strategy without letting the context know about the concrete class's details."
> * **Variables to watch**:
>   * `checkoutContext` (CheckoutContext): Inspect that the context object is instantiated and not null.
>   * `shippingStrategy` (ShippingStrategy): Observe that this is the instance about to be bound to the context.

---

### 📌 Breakpoint 3: Delegating Shipping Fee Execution
* **File**: [CheckoutContext.java](file:///c:/Users/saraf/Downloads/[CSE6234%20SOFT.%20DESIGN]/assignment/busana/src/main/java/com/busana/service/strategy/CheckoutContext.java#L14-L16)
* **Location**: Line 15 (inside `executeShipping()`)
* **Code Segment**:
  ```java
  return shippingStrategy.calculateShippingFee();
  ```

#### What to inspect and say:
> **Explanation**: "This breakpoint demonstrates polymorphism. By stepping into `checkoutContext.executeShipping()`, we verify that control is forwarded directly to `shippingStrategy.calculateShippingFee()`. Even though the context only knows about the `ShippingStrategy` interface, the JVM calls the correct subclass method at runtime."
> * **Variables to watch**:
>   * `shippingStrategy` (Polymorphic Field): Hover over this to see the runtime type (e.g. `SameDayShipping`).
>   * **Step Into**: Step into the call and verify you land on the correct implementation (e.g., returning `30.0` for Same Day, `15.0` for Express, or `5.0` for Standard).

---

### 📌 Breakpoint 4: Pricing Strategy Selection (Regular vs. Promotional)
* **File**: [CartWishlistService.java](file:///c:/Users/saraf/Downloads/[CSE6234%20SOFT.%20DESIGN]/assignment/busana/src/main/java/com/busana/service/CartWishlistService.java#L308-L310) or [CartWishlistService.java](file:///c:/Users/saraf/Downloads/[CSE6234%20SOFT.%20DESIGN]/assignment/busana/src/main/java/com/busana/service/CartWishlistService.java#L356-L357)
* **Location**: Line 309 or Line 356 (inside `calculateCheckout`)
* **Code Segment**:
  ```java
  PricingStrategy pricingStrategy = new PromotionalPricing(promo.getDiscountValue().doubleValue(), promo.getDiscountType());
  // OR
  PricingStrategy pricingStrategy = new RegularPricing();
  ```

#### What to inspect and say:
> **Explanation**: "This breakpoint verifies the selection of the pricing strategy based on promotion presence. If promotions exist, a `PromotionalPricing` strategy is instantiated with the discount details. If not, the context falls back to a `RegularPricing` strategy."
> * **Variables to watch**:
>   * `promotions` (List<Promotion>): Verify if any promotions were retrieved/applied.
>   * `promo.getDiscountValue()`, `promo.getDiscountType()` (if promotional): Confirm the promo data matches the database parameters (e.g. `10.0` Fixed or `20.0` Percentage).
>   * `pricingStrategy` (PricingStrategy): Confirm it instantiates the correct subclass.

---

### 📌 Breakpoint 5: Pricing Calculation Execution
* **File**: [PromotionalPricing.java](file:///c:/Users/saraf/Downloads/[CSE6234%20SOFT.%20DESIGN]/assignment/busana/src/main/java/com/busana/service/strategy/PromotionalPricing.java#L15-L20)
* **Location**: Lines 15–20 (inside `calculatePrice()`)
* **Code Segment**:
  ```java
  double finalPrice = (discountType.equalsIgnoreCase("fixed") || discountType.equalsIgnoreCase("flat"))
          ? basePrice - discountValue
          : (100 - discountValue)/100 * basePrice;
  return Math.max(finalPrice, 0);
  ```

#### What to inspect and say:
> **Explanation**: "This breakpoint shows the calculation algorithm running inside the concrete strategy class. Step in to verify if the formula for a flat discount or percentage discount is correctly chosen based on `discountType`, and that the return value is clamped to at least zero to avoid negative pricing."
> * **Variables to watch**:
>   * `basePrice` (double): The initial subtotal eligible for the promotion.
>   * `discountValue` (double): The discount multiplier or flat rate.
>   * `discountType` (String): e.g. `"fixed"`, `"flat"`, or `"percentage"`.
>   * `finalPrice` (double): Inspect this to confirm the mathematical correctness of the applied formula.

---

### 📌 Breakpoint 6: Checkout Result Assembly
* **File**: [CartWishlistService.java](file:///c:/Users/saraf/Downloads/[CSE6234%20SOFT.%20DESIGN]/assignment/busana/src/main/java/com/busana/service/CartWishlistService.java#L361-L362)
* **Location**: Line 361–362
* **Code Segment**:
  ```java
  double totalAmount = discountedSubtotal + shippingFee;
  return new CheckoutResult(subtotal, discountAmount, shippingFee, totalAmount);
  ```

#### What to inspect and say:
> **Explanation**: "This final breakpoint confirms that the calculations from both the pricing strategy and shipping strategy are successfully integrated. We inspect the values inside the `CheckoutResult` record to verify the final sum is mathematically correct before returning it to the checkout view."
> * **Variables to watch**:
>   * `subtotal` (double): Original items total.
>   * `discountAmount` (double): Total savings calculated from `PricingStrategy`.
>   * `shippingFee` (double): Fee calculated from `ShippingStrategy`.
>   * `totalAmount` (double): Expected final amount = (subtotal - discountAmount) + shippingFee.