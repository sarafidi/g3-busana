-- ======================================================
-- Create database 
-- ======================================================

CREATE DATABASE IF NOT EXISTS busana;
USE busana;

-- ======================================================
-- SECTION 1: BASE INDEPENDENT TABLES
-- ======================================================

-- 1. Customer Table
CREATE TABLE Customer (
    customerID VARCHAR(20) NOT NULL,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(20) NOT NULL,
    deliveryAddress VARCHAR(255) NOT NULL,
    PRIMARY KEY (customerID)
)

-- 2. Admin Table
CREATE TABLE Admin (
    adminID VARCHAR(20) NOT NULL,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(20) NOT NULL,
    PRIMARY KEY (adminID)
)

-- 3. Category Table
CREATE TABLE Category (
    categoryID VARCHAR(20) NOT NULL,
    categoryName VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    PRIMARY KEY (categoryID)
)

-- ======================================================
-- SECTION 2: CORE PRODUCTS AND INVENTORY TABLE
-- ======================================================

-- 4. Product Table
CREATE TABLE Product (
    productID VARCHAR(20) NOT NULL,
    categoryID VARCHAR(20) NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    basePrice DECIMAL(10, 2) NOT NULL,
    fabricType VARCHAR(100) NOT NULL,
    images VARCHAR(255),
    status VARCHAR(20) NOT NULL,
    PRIMARY KEY (productID),
    Foreign Key (categoryID) REFERENCES Category(categoryID) ON DELETE CASCADE
)

-- 5. Product Variant Table (Variant sizes/colours inventory)
CREATE TABLE ProductVariant (
    variantID VARCHAR(20) NOT NULL,
    productID VARCHAR(20) NOT NULL,
    size VARCHAR(5) NOT NULL,
    colour VARCHAR(20) NOT NULL,
    stockLevel INT NOT NULL DEFAULT 0,
    PRIMARY KEY(variantID),
    Foreign Key (productID) REFERENCES Product(productID) ON DELETE CASCADE
)

-- ======================================================
-- SECTION 3: TRANSITION & RELATIONAL TABLES
-- ======================================================

-- 6. Wishlist Table
CREATE TABLE Wishlist (
    wishlistID VARCHAR(20) NOT NULL,
    customerID VARCHAR(20) NOT NULL,
    PRIMARY KEY(wishlistID),
    Foreign Key (customerID) REFERENCES Customer(customerID) ON DELETE CASCADE
)

-- 7. Wishlist Item Table
CREATE TABLE WishlistItem (
    wishlistItemID VARCHAR(20) NOT NULL,
    variantID VARCHAR(20) NOT NULL,
    PRIMARY KEY(wishlistItemID),
    Foreign Key (variantID) REFERENCES ProductVariant(variantID) ON DELETE CASCADE
)

-- 8. Shopping Cart Table
CREATE TABLE ShoppingCart (
    cartID VARCHAR(20) NOT NULL,
    customerID VARCHAR(20) NOT NULL,
    PRIMARY KEY(cartID),
    Foreign Key (customerID) REFERENCES Customer(customerID) ON DELETE CASCADE
)

-- 9. Cart Item Table
CREATE TABLE CartItem (
    cartID VARCHAR(20) NOT NULL,
    cartItemID VARCHAR(20) NOT NULL,
    variantID VARCHAR(20) NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    subtotal INT NOT NULL DEFAULT 0,
    PRIMARY KEY(cartItemID),
    Foreign Key (cartID) REFERENCES ShoppingCart(cartID) ON DELETE CASCADE,
    Foreign Key (variantID) REFERENCES ProductVariant(variantID) ON DELETE CASCADE
)

-- 10. Promotion Table
CREATE TABLE Promotion (
    promotionID VARCHAR(20) NOT NULL,
    promotionName VARCHAR(100) NOT NULL,
    discountType VARCHAR(20) NOT NULL,
    discountValue DECIMAL(10, 2) NOT NULL,
    applicableCategory VARCHAR(100),
    startDate DATE NOT NULL,
    endDate DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'active',
    PRIMARY KEY (promotionID)
)

-- ======================================================
-- SECTION 4: TRANSACTION & SYSTEM NOTIFICATION
-- ======================================================

-- 11. Order Table
CREATE TABLE `Order` (
    orderID VARCHAR(20) NOT NULL,
    customerID VARCHAR(20) NOT NULL,
    promotionID VARCHAR(20),
    orderDate DATE NOT NULL,
    orderStatus VARCHAR(20) NOT NULL DEFAULT 'Pending',
    shippingFee DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    totalAmount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    deliveryAddress VARCHAR(255) NOT NULL,
    paymentStatus VARCHAR(20) NOT NULL DEFAULT 'Pending',
    PRIMARY KEY(orderID),
    Foreign Key (customerID) REFERENCES Customer(customerID) ON DELETE CASCADE,
    Foreign Key (promotionID) REFERENCES Promotion(promotionID) ON DELETE CASCADE
)

-- 12. Order Item Table
CREATE TABLE OrderItem (
    orderItemID VARCHAR(20) NOT NULL,
    orderID VARCHAR(20) NOT NULL,
    variantID VARCHAR(20) NOT NULL,
    quantity INT NOT NULL DEFAULT 0,
    subtotal DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    PRIMARY KEY(orderItemID),
    Foreign Key (orderID) REFERENCES `Order`(orderID) ON DELETE CASCADE,
    Foreign Key (variantID) REFERENCES ProductVariant(variantID) ON DELETE CASCADE
)

-- 13. Notification
CREATE TABLE Notification (
    notificationID VARCHAR(20) NOT NULL,
    orderID VARCHAR(20) NOT NULL,
    customerID VARCHAR(20) NOT NULL,
    message VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'unread',
    PRIMARY KEY(notificationID),
    Foreign Key (orderID) REFERENCES `Order`(orderID) ON DELETE CASCADE,
    Foreign Key (customerID) REFERENCES Customer(customerID) ON DELETE CASCADE
)