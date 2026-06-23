package com.busana.service;

import com.busana.model.*;
import com.busana.repository.*;
import com.busana.service.strategy.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

@Service
public class CartWishlistService {
    @Autowired
    private CheckoutContext checkoutContext;
    /*
        Exception checks:
        1. is customer logged in?       -> check session in controller, not service
        2. is variant in stock?         -> variant.getStockLevel() > 0
        3. does quantity exceed stock   -> requestedQty <= variant.getStockLevel()
    */

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private WishlistItemRepository wishlistItemRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    /*
        add a specified product variant to customer's cart,
        check if variant exists and if sufficient stock is available
    */
    public CartItem addToCart(String customerID, String variantID, int quantity) {
        // step 1 and 2: find ProductVariant by variantID and check if variant exists
        ProductVariant variant = productVariantRepository.findById(variantID)
                .orElseThrow(() -> new RuntimeException("Variant not found!"));

        // step 3: check if variant is in stock
        if (!variant.isAvailable())
            throw new RuntimeException("Variant is not in stock!");
        // step 4: check if quantity exceeds stock
        if (variant.getStockLevel() < quantity)
            throw new RuntimeException("Quantity exceeds variant stock level!");

        // step 5: find/create customer's shoppingCart
        ShoppingCart shoppingCart = shoppingCartRepository.findByCustomer_CustomerID(customerID)
                .orElseGet(() -> {
                    Customer customer = new Customer();
                    customer.setCustomerID(customerID);
                    ShoppingCart newShoppingCart = new ShoppingCart("CART-" + customerID, customer);
                    return shoppingCartRepository.save(newShoppingCart);
                });

        // step 6: check if same variant is already in cart
        List<CartItem> cartItems = cartItemRepository.findByCart_CartID(shoppingCart.getCartID());
        Optional<CartItem> existingItem = cartItems.stream()
                .filter(item -> item.getVariant().getVariantID().equalsIgnoreCase(variantID))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem itemToUpdate = existingItem.get();
            int newQuantity = itemToUpdate.getQuantity() + quantity;

            if (newQuantity > variant.getStockLevel())
                throw new RuntimeException("Not enough stock for requested quantity!");
            itemToUpdate.setQuantity(newQuantity);

            // step 7: calculate subtotal for item with newQuantity
            itemToUpdate.setSubtotal((int)(variant.getProduct().getBasePrice().doubleValue() * newQuantity));
            // step 8: create and save updated cart item
            return cartItemRepository.save(itemToUpdate);
        } else {
            // step 7: calculate subtotal for new item
            int subtotal = (int)(variant.getProduct().getBasePrice().doubleValue() * quantity);
            CartItem newItem = new CartItem(
                    "CI" + System.currentTimeMillis(),
                    shoppingCart,
                    variant,
                    quantity,
                    subtotal
            );
            return cartItemRepository.save(newItem);
        }
    }

    /*
        retrieves all items currently stored in customer's cart,
        verifying that the cart belongs to correct user
    */
    public List<CartItem> viewCart(String customerID) {
        // step 1: find customer's cart
        Optional<ShoppingCart> shoppingCart = shoppingCartRepository.findByCustomer_CustomerID(customerID);
        // step 2: return empty list if no cart exists
        if (shoppingCart.isEmpty()) return List.of();
        // step 3: return all items in the cart if cart exists
        return cartItemRepository.findByCart_CartID(shoppingCart.get().getCartID());
    }

    /*
        updates the quantity of specific item in cart,
        checking if new quantity is valid and within stock limits
    */
    public CartItem updateCartItem(String customerID, String cartItemID, int quantity) {
        // step 1: find CartItem by cartItemID
        CartItem cartItem = cartItemRepository.findById(cartItemID)
                .orElseThrow(() -> new RuntimeException("Cart Item not found!"));
        ShoppingCart cart = cartItem.getCart();

        // step 2: verify item belongs to current customer's cart
        if (!cart.getCustomer().getCustomerID().equals(customerID))
            throw new RuntimeException("Unauthorised access to cart item!");

        // step 3: check new quantity is at least 1
        if (quantity < 1)
            throw new RuntimeException("Quantity is less than 1!");

        // step 4: check new quantity doesn't exceed stock
        if (quantity > cartItem.getVariant().getStockLevel())
            throw new RuntimeException("Quantity exceeds stock!");

        // step 5: update quantity and recalculate subtotal
        Product product = cartItem.getVariant().getProduct();
        cartItem.setQuantity(quantity);
        cartItem.setSubtotal((int)(product.getBasePrice().doubleValue() * quantity));

        // step 6: save and return
        return cartItemRepository.save(cartItem);
    }

    /*
        removes an item from cart, verify item exists
        and connected with customer's current cart
    */
    public void removeFromCart(String customerID, String cartItemID) {
        // step 1: find CartItem by cartItemID
        CartItem cartItem = cartItemRepository.findById(cartItemID)
                .orElseThrow(() -> new RuntimeException("Cart Item not found!"));
        ShoppingCart cart = cartItem.getCart();

        // step 2: verify this belongs to current customer
        if (!cart.getCustomer().getCustomerID().equals(customerID))
            throw new RuntimeException("Unauthorised access to cart item!");

        // step 3: delete CartItem
        cartItemRepository.delete(cartItem);
    }

    /*
        adds a product variant to customer's wishlist,
        check if variant is already present to prevent duplicates
    */
    public WishlistItem addToWishlist(String customerID, String variantID) {
        // step 1 and 2: find ProductVariant by variantID and check if variant exists
        ProductVariant variant = productVariantRepository.findById(variantID)
                .orElseThrow(() -> new RuntimeException("Variant not found!"));

        // step 3: check if variant is in stock
        if (!variant.isAvailable())
            throw new RuntimeException("Variant is not in stock!");

        // step 4: find/create customer's wishlist
        Wishlist wishlist = wishlistRepository.findByCustomer_CustomerID(customerID)
                .orElseGet(() -> {
                    Customer customer = new Customer();
                    customer.setCustomerID(customerID);
                    Wishlist newWishlist = new Wishlist("WI-" + customerID, customer);
                    return wishlistRepository.save(newWishlist);
                });

        // step 5: check if same variant is already in cart
        List<WishlistItem> wishlistItems = wishlistItemRepository.findByWishlist_WishlistID(wishlist.getWishlistID());
        Optional<WishlistItem> existingItem = wishlistItems.stream()
                .filter(item -> item.getVariant().getVariantID().equalsIgnoreCase(variantID))
                .findFirst();

        if (existingItem.isPresent())
            throw new RuntimeException("Variant already in wishlist!");

        WishlistItem newWishlistItem = new WishlistItem(
                "WI-" + System.currentTimeMillis(),
                wishlist,
                variant
        );
        return wishlistItemRepository.save(newWishlistItem);
    }

    public boolean toggleWishlist(String customerID, String variantID) {
        ProductVariant variant = productVariantRepository.findById(variantID)
                .orElseThrow(() -> new RuntimeException("Variant not found!"));

        Wishlist wishlist = wishlistRepository.findByCustomer_CustomerID(customerID)
                .orElseGet(() -> {
                    Customer customer = new Customer();
                    customer.setCustomerID(customerID);
                    Wishlist newWishlist = new Wishlist("WI-" + customerID, customer);
                    return wishlistRepository.save(newWishlist);
                });

        List<WishlistItem> wishlistItems = wishlistItemRepository.findByWishlist_WishlistID(wishlist.getWishlistID());
        Optional<WishlistItem> existingItem = wishlistItems.stream()
                .filter(item -> item.getVariant().getVariantID().equalsIgnoreCase(variantID))
                .findFirst();

        if (existingItem.isPresent()) {
            wishlistItemRepository.delete(existingItem.get());
            return true; // removed
        } else {
            if (!variant.isAvailable())
                throw new RuntimeException("Variant is not in stock!");

            WishlistItem newWishlistItem = new WishlistItem(
                    "WI-" + System.currentTimeMillis(),
                    wishlist,
                    variant
            );
            wishlistItemRepository.save(newWishlistItem);
            return false; // added
        }
    }

    /*
        removes a product variant from customer's wishlist,
        check if wishlist item exists before attempting deletion
    */
    public void removeFromWishlist(String customerID, String wishlistItemID) {
        // step 1: find WishlistItem by wishlistID
        WishlistItem wishlistItem = wishlistItemRepository.findById(wishlistItemID)
                .orElseThrow(() -> new RuntimeException("Wishlist Item not found!"));
        Wishlist wishlist = wishlistItem.getWishlist();

        // step 2: verify this belongs to current customer
        if (!wishlist.getCustomer().getCustomerID().equals(customerID))
            throw new RuntimeException("Unauthorised access to wishlist item!");

        // step 3: delete WishlistItem
        wishlistItemRepository.delete(wishlistItem);
    }

    /*
        retrieves all items currently stored in customer's wishlist,
        verifying that the cart belongs to correct user before returning list
    */
    public List<WishlistItem> viewWishlist(String customerID) {
        // step 1: find customer's wishlist
        Optional<Wishlist> wishlist = wishlistRepository.findByCustomer_CustomerID(customerID);
        // step 2: return empty list if no wishlist exists
        if (wishlist.isEmpty()) return List.of();
        // step 3: return all items in the wishlist if wishlist exists
        return wishlistItemRepository.findByWishlist_WishlistID(wishlist.get().getWishlistID());
    }

    public void clearCart(String customerID) {
        // step 1: find customer's Cart
        ShoppingCart shoppingCart = shoppingCartRepository.findByCustomer_CustomerID(customerID)
                .orElse(null);
        if (shoppingCart == null) return;

        // step 2: get all items in cart
        List<CartItem> cartItems = cartItemRepository.findByCart_CartID(shoppingCart.getCartID());

        // step 3: delete all items
        cartItemRepository.deleteAll(cartItems);
    }

    public CheckoutResult calculateCheckout(List<CartItem> cartItems, String shippingMethod) {
        return calculateCheckout(cartItems, shippingMethod, List.of());
    }

    public CheckoutResult calculateCheckout(List<CartItem> cartItems, String shippingMethod, Promotion promotion) {
        return calculateCheckout(cartItems, shippingMethod, promotion != null ? List.of(promotion) : List.of());
    }

    public CheckoutResult calculateCheckout(List<CartItem> cartItems, String shippingMethod, List<Promotion> promotions) {
        double subtotal = cartItems.stream().mapToDouble(CartItem::getSubtotal).sum();

        // 1. Select concrete Shipping Strategy
        ShippingStrategy shippingStrategy;
        if ("express".equalsIgnoreCase(shippingMethod)) {
            shippingStrategy = new ExpressShipping();
        } else if ("sameday".equalsIgnoreCase(shippingMethod)) {
            shippingStrategy = new SameDayShipping();
        } else {
            shippingStrategy = new StandardShipping();
        }
        checkoutContext.setShippingStrategy(shippingStrategy);
        double shippingFee = checkoutContext.executeShipping();

        // 2. Select concrete Pricing Strategy and apply in sequence
        double discountedSubtotal;
        double discountAmount = 0.0;

        if (promotions != null && !promotions.isEmpty()) {
            double[] itemPrices = cartItems.stream().mapToDouble(CartItem::getSubtotal).toArray();

            for (Promotion promo : promotions) {
                applyPromotion(itemPrices, cartItems, promo);
            }

            discountedSubtotal = java.util.Arrays.stream(itemPrices).sum();
            discountAmount = subtotal - discountedSubtotal;
        } else {
            checkoutContext.setPricingStrategy(new RegularPricing());
            discountedSubtotal = checkoutContext.executePrice(subtotal);
        }

        double totalAmount = discountedSubtotal + shippingFee;
        return new CheckoutResult(subtotal, discountAmount, shippingFee, totalAmount);
    }

    private void applyPromotion(double[] itemPrices, List<CartItem> cartItems, Promotion promo) {
        PricingStrategy pricingStrategy = new PromotionalPricing(promo.getDiscountValue().doubleValue(), promo.getDiscountType());
        checkoutContext.setPricingStrategy(pricingStrategy);

        String applicableCategory = promo.getApplicableCategory();
        boolean isCategorySpecific = applicableCategory != null && !applicableCategory.trim().isEmpty();

        // 1. Calculate eligible subtotal
        double eligibleSubtotal = 0.0;
        for (int i = 0; i < cartItems.size(); i++) {
            if (!isCategorySpecific || isItemInCategory(cartItems.get(i), applicableCategory)) {
                eligibleSubtotal += itemPrices[i];
            }
        }

        if (eligibleSubtotal <= 0) return;

        // 2. Apply strategy and calculate reduction factor
        double finalEligibleSubtotal = checkoutContext.executePrice(eligibleSubtotal);
        double factor = finalEligibleSubtotal / eligibleSubtotal;

        // 3. Proportional scale-down of prices
        for (int i = 0; i < cartItems.size(); i++) {
            if (!isCategorySpecific || isItemInCategory(cartItems.get(i), applicableCategory)) {
                itemPrices[i] *= factor;
            }
        }
    }

    private boolean isItemInCategory(CartItem item, String category) {
        Product product = item.getVariant().getProduct();
        String categoryName = product.getCategory() != null ? product.getCategory().getCategoryName() : "";
        return categoryName.equalsIgnoreCase(category.trim());
    }

    public record CheckoutResult(double subtotal, double discountAmount, double shippingFee, double totalAmount) {}
}