# Strategy Pattern Debugging & Breakpoint Guide (Service-Delegated)

This guide explains how the **Strategy Pattern** is applied to your shipping options (`ShippingStrategy`) and teaches you exactly how to demonstrate it to your lecturer step-by-step using debugger breakpoints in IntelliJ IDEA (or VS Code).

In this implementation, the project follows clean architecture principles (Thin Controller / Fat Service): the strategy logic has been delegated from the controller to the service layer.

---

## 1. Strategy Pattern Architecture in Your Code

To explain the pattern to your lecturer, you should identify these key components:

1. **The Client**: [CartWishlistService.java](file:///c:/Users/saraf/Downloads/[CSE6234%20SOFT.%20DESIGN]/assignment/busana/src/main/java/com/busana/service/CartWishlistService.java) (specifically the `calculateCheckout()` method)
   - Instantiates a concrete strategy based on the customer's selection and passes it to the `CheckoutContext`.
2. **The Context**: [CheckoutContext.java](file:///c:/Users/saraf/Downloads/[CSE6234%20SOFT.%20DESIGN]/assignment/busana/src/main/java/com/busana/service/pattern/CheckoutContext.java)
   - Maintains a reference to a `ShippingStrategy` object and delegates the execution to it at runtime.
3. **The Strategy Interface**: [ShippingStrategy.java](file:///c:/Users/saraf/Downloads/[CSE6234%20SOFT.%20DESIGN]/assignment/busana/src/main/java/com/busana/service/pattern/ShippingStrategy.java)
   - Declares the common method `calculateShippingFee()` that all concrete strategies must implement.
4. **Concrete Strategies**:
   - [StandardShipping.java](file:///c:/Users/saraf/Downloads/[CSE6234%20SOFT.%20DESIGN]/assignment/busana/src/main/java/com/busana/service/pattern/StandardShipping.java) (returns RM 5.00)
   - [ExpressShipping.java](file:///c:/Users/saraf/Downloads/[CSE6234%20SOFT.%20DESIGN]/assignment/busana/src/main/java/com/busana/service/pattern/ExpressShipping.java) (returns RM 15.00)
   - [SameDayShipping.java](file:///c:/Users/saraf/Downloads/[CSE6234%20SOFT.%20DESIGN]/assignment/busana/src/main/java/com/busana/service/pattern/SameDayShipping.java) (returns RM 30.00)

---

## 2. Where to Set Your Breakpoints

Open your IDE (IntelliJ IDEA is recommended) and click on the gutter (left-hand margin next to the line numbers) to set red breakpoint circles at these exact locations:

### Breakpoint 1: The Starting Point (The Client)
* **File**: [CartWishlistService.java](file:///c:/Users/saraf/Downloads/[CSE6234%20SOFT.%20DESIGN]/assignment/busana/src/main/java/com/busana/service/CartWishlistService.java)
* **Line**: `240` (inside the `calculateCheckout` method)
* **Code line**: `if ("express".equalsIgnoreCase(shippingMethod)) {`
* **Purpose**: Capture the process immediately when the service starts processing the checkout.

### Breakpoint 2: The Context Delegation
* **File**: [CheckoutContext.java](file:///c:/Users/saraf/Downloads/[CSE6234%20SOFT.%20DESIGN]/assignment/busana/src/main/java/com/busana/service/pattern/CheckoutContext.java)
* **Line**: `15` (inside the `executeShipping` method)
* **Code line**: `return shippingStrategy.calculateShippingFee();`
* **Purpose**: Show that the context does not know which shipping calculation it runs—it delegates to the polymorphic interface.

### Breakpoint 3: Concrete Strategy calculation
* **File**: [ExpressShipping.java](file:///c:/Users/saraf/Downloads/[CSE6234%20SOFT.%20DESIGN]/assignment/busana/src/main/java/com/busana/service/pattern/ExpressShipping.java)
* **Line**: `9`
* **Code line**: `return 15.00;`
* **Purpose**: Show that the debugger steps exactly into the selected concrete implementation.

---

## 3. Step-by-Step Demonstration Flow

Follow these steps to run the demo:

### Step 1: Run the Server in Debug Mode
1. In IntelliJ, click the **bug icon** (Debug 'BusanaApplication') to run the Spring Boot app.
2. Ensure your local MySQL DB is running.

### Step 2: Trigger the Flow in Your Browser
1. Open your browser and go to: `http://localhost:8080/`
2. Log in as a customer, add some items to your cart, and go to the **Cart** page.
3. Click the **Proceed to Checkout** button.
4. On the checkout screen, enter a delivery address.
5. Select **Express Shipping** as the method.
6. Click the **Place Order & Pay** button.

### Step 3: Debugging Walkthrough (Show your Lecturer)

As soon as you click the button, your IDE will pop up and suspend execution at **Breakpoint 1** in `CartWishlistService.java`. Explain the following steps to your lecturer:

1. **Inspect the Selected Method**:
   - Point to the **Variables** panel at the bottom of the IDE.
   - Show that `shippingMethod` has the value `"express"`.

2. **Step Over to See Instantiation**:
   - Click **Step Over** (Shortcut: **F8** in IntelliJ).
   - The execution line jumps inside the `"express"` conditional block and instantiates `strategy = new ExpressShipping()`.
   - Point to the `strategy` variable in the IDE to show its dynamic type is `ExpressShipping`.

3. **Step Into setting the Strategy**:
   - Keep stepping over until you reach the line:
     `checkoutContext.setShippingStrategy(strategy);`
   - Click **Step Into** (Shortcut: **F7** in IntelliJ).
   - This takes you into `CheckoutContext.java` at line 10, setting the `shippingStrategy` field to the `ExpressShipping` instance. 
   - Click **Step Out** (Shortcut: **Shift + F8**) to return to the service.

4. **Step Into the Execution (Polymorphism)**:
   - In the service class, stop at:
     `double shippingFee = checkoutContext.executeShipping();`
   - Click **Step Into** (**F7**). You will land on **Breakpoint 2** inside `CheckoutContext.java`:
     `return shippingStrategy.calculateShippingFee();`
   - **Crucial Explanation**: *Tell your lecturer: "Notice that this line calls `calculateShippingFee()` on the `ShippingStrategy` interface. The context doesn't hardcode any rates. It executes whichever strategy is set at runtime."*

5. **Step Into the Concrete Implementation**:
   - With the cursor on `return shippingStrategy.calculateShippingFee();`, click **Step Into** (**F7**).
   - The debugger will jump directly to **Breakpoint 3** inside [ExpressShipping.java](file:///c:/Users/saraf/Downloads/[CSE6234%20SOFT.%20DESIGN]/assignment/busana/src/main/java/com/busana/service/pattern/ExpressShipping.java):
     `return 15.00;`
   - *Tell your lecturer: "Since we selected Express Shipping in the UI, polymorphism dynamically routed this call to ExpressShipping. If we had selected Same-Day, it would have routed to SameDayShipping."*

6. **Complete the Execution**:
   - Click the **Resume Program** button (green arrow, shortcut **F9**).
   - The browser will resume and show you the **Order Confirmed** receipt page with the correct shipping fee break-down of `RM 15.00` and the final total!
