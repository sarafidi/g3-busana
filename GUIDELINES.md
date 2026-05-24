# GUIDELINES - READ THIS FIRST BEFORE STARTING

### 📋SETUP - WHAT HAS BEEN DONE
| | Task | Details |
| --- | --- | --- |
|✅ | Spring Boot 4.0.6 project scaffolded with Maven | Java 26 · Tomcat on port 3306 · package `com.busana` |
|✅ | MySQL database created · connected and verified | DB name: `busana` · MySQL 8.0.46 · all 13 tables created |
|✅ | `application.properties` configured (not committed to GitHub) | Copy `application.properties.example` → rename to `application.properties` → fill in your own MySQL password |
|✅ | Thymeleaf chosen as rendering approach | Controllers return HTML views · no separate frontend framework needed |
|✅ | `Category.java` model created as example — follow this pattern for  all models | Place all models in `src/main/java/com/busana/model/` |
|✅ | `CategoryRepository.java` created as example — follow this pattern  for all repositories | Place all repositories in `src/main/java/com/busana/repository/` |
|✅ | Folder structure created for templates and static files | `templates/customer/` · `templates/admin/` · `templates/shared/` · `static/css/` · `static/js/` · `static/images/` |
|✅ | `home.html` created as Thymeleaf template example | Shows how to use `th:text`, `th:href`, `th:each`, `th:if` — use as base for all pages |
|✅ | GitHub repo set up · app builds and starts successfully | Run `./mvnw spring-boot:run` to start · visit `localhost:3306` |
---
<br>

### 📋SETUP - WHAT HAS BEEN DONE
| | Rule | Details |
| --- | --- | --- |
|⚠️ | Never commit `application.properties` to GitHub — it contains your DB password | It is already in `.gitignore`. Each person sets up their own local copy. |
|⚠️ | FK fields in models use `@ManyToOne` + `@JoinColumn` — never `@Column` | See `Product.java` for the correct pattern. Wrong annotation = app won't start. |
|⚠️ | Column names in `@Column(name="...")` must match SQL exactly (camelCase) | e.g. `categoryName` not `category_name` |
|⚠️ | Always pull from GitHub before starting work each session | `git pull origin main` — avoid merge conflicts |
|⚠️ | Use money type `BigDecimal` for all price/fee fields — never `double` or `float` | Applies to `basePrice`, `shippingFee`, `totalAmount`, `discountValue`, `subtotal` |
|⚠️ | Use `LocalDate` for all date fields — never `String` or `Date` | Applies to `orderDate`, `startDate`, `endDate` |
---
<br>

### 📋PACKAGE STRUCTURE - WHERE EACH FILE GOES
| | |
| --- | --- |
|📁 | `com.busana.model` → JPA entity classes (one per DB table) |
|📁 | `com.busana.repository` → Repository interfaces (one per model) |
|📁 | `com.busana.service` → Business logic classes (one per feature module) |
|📁 | `com.busana.controller` → HTTP controllers (one per feature module) |
|📁 | `src/main/resources/templates/customer/` → customer-facing HTML pages |
|📁 | `src/main/resources/templates/admin/` → admin panel HTML pages |
|📁 | `src/main/resources/static/css/` · `static/js/` · `static/images/` |
---
<br>

### TASK MODULE - PICK ONE AND OWN IT END-TO-END
- Each module includes models + repository + service + controller + HTML pages + your assigned design pattern.
- Reference the pattern's class diagram, sequence diagram, and sample code from Assignment 1.

<br>

========================================================================
#### 👤 Module A — Auth + DB (Singleton)
*2 models • 2 repositories • 3 pages*

#### <u >Models</u>
📁 `Customer.java`, `Admin.java`
#### <u>Repositories</u>
📁 `CustomerRepository` — add `findByEmail()` \
📁 `AdminRepository` — add `findByEmail()`
#### <u>Service + Controller</u>
📁 Customer login · register · logout \
📁 Admin login · logout
#### <u>Design pattern — Singleton</u>
❗`DBConnection.java` — single DB connection instance used across the whole app
#### <u>Pages</u>
🎨 `customer/login.html`, `customer/register.html`, `admin/login.html`

<br>

---
#### 👚 Module B — Product catalogue (Factory Method)
*3 models • 3 repositories • 4 pages*

#### <u >Models</u>
📁 `Product.java`, `ProductVariant.java`, `Category.java` (example already done)
#### <u>Repositories</u>
📁 `ProductRepository` — add `findByStatus()`, `findByCategory_CategoryID()` \
📁 `ProductVariantRepository` — add `findByProduct_ProductID()` \
📁 `CategoryRepository` (example already done)
#### <u>Service + Controller</u>
📁 Customer: view catalogue · search · filter by category/size/colour/price \
📁 Admin: add · edit · delete · update stock per variant
#### <u>Design pattern — Factory Method</u>
❗`ProductFactory.java` interface + `TopsFactory`, `BottomsFactory`, `AccessoriesFactory`
#### <u>Pages</u>
🎨 `customer/product-list.html`, `customer/product-detail.html`, `admin/product-list.html`, `admin/product-form.html`

<br>

---
#### 🛒 Module C — Cart, wishlist & checkout (Strategy)
*4 models • 4 repositories • 3 pages*

#### <u >Models</u>
📁 `ShoppingCart.java`, `CartItem.java`, `Wishlist.java`, `WishlistItem.java`
#### <u>Repositories</u>
📁 `ShoppingCartRepository`, `CartItemRepository` — add `findByCart_CartID()` \
📁 `WishlistRepository`, `WishlistItemRepository`
#### <u>Service + Controller</u>
📁 Add · view · update · delete cart items \
📁 Add · view · remove wishlist items \
📁 Checkout flow: confirm address · select shipping · order summary
#### <u>Design pattern — Strategy</u>
❗`ShippingStrategy.java`, `PricingStrategy.java` interfaces \
❗`StandardShipping`, `ExpressShipping`, `SameDayShipping`, `RegularPricing`, `PromotionalPricing` \
❗`CheckoutContext.java` — sets and executes strategies at runtime 
#### <u>Pages</u>
🎨 `customer/cart.html`, `customer/wishlist.html`, `customer/checkout.html`

<br>

---
#### 📦 Module D — Orders, promotions & notifications (Observer)
*3 models • 3 repositories • 4 pages*

#### <u >Models</u>
📁 Order.java, OrderItem.java, Promotion.java, Notification.java
#### <u>Repositories</u>
📁 `OrderRepository` — add `findByCustomer_CustomerID()` \
📁 `OrderItemRepository`, `PromotionRepository` \
📁 `NotificationRepository` — add `findByCustomer_CustomerID()`
#### <u>Service + Controller</u>
📁 Admin: view orders · update order status · create/edit/delete promotions \
📁 Customer: order confirmation page
#### <u>Design pattern — Observer</u>
❗`OrderSubject.java`, `OrderObserver.java` interfaces \
❗`OrderManager.java` — notifies observers when order status changes \
❗`EmailNotificationObserver.java` — sends email to customer on status update
#### <u>Pages</u>
🎨 `customer/order-confirmation.html`, `admin/order-list.html`, `admin/order-detail.html`, `admin/promotion-list.html`, `admin/promotion-form.html`, `admin/dashboard.html`

---
<br><br>

### ✅EVERY PERSON'S CHECKLIST MODULE
[  ] Create model class(es) — follow `Category.java` as example \
[  ] Create repository interface(s) — follow `CategoryRepository.java` as example · add custom queries listed above \
[  ] Implement your assigned design pattern classes in `com.busana.service` — refer to Assignment 1 sample code \
[  ] Create service class with business logic \
[  ] Create controller class — calls service, passes data to view via Model, returns template name \
[  ] Build HTML pages using `home.html` as base template \
[  ] Test locally · run `./mvnw spring-boot:run` · confirm no errors · then push to GitHub