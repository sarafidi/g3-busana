package com.busana.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.busana.service.factory.ProductFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.busana.model.Category;
import com.busana.model.Product;
import com.busana.model.ProductVariant;
import com.busana.repository.CategoryRepository;
import com.busana.repository.ProductRepository;
import com.busana.repository.ProductVariantRepository;

@Service
public class ProductService {
    private static final String ACTIVE_STATUS = "active";
    private static final String INACTIVE_STATUS = "inactive";

    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final CategoryRepository categoryRepository;
    private final Map<String, ProductFactory> factoryMap;

    public ProductService(
        ProductRepository productRepository,
        ProductVariantRepository productVariantRepository,
        CategoryRepository categoryRepository,
        List<ProductFactory> productFactories
    ) {
        this.productRepository = productRepository;
        this.productVariantRepository = productVariantRepository;
        this.categoryRepository = categoryRepository;
        this.factoryMap = new HashMap<>();

        for (ProductFactory productFactory : productFactories) {
            factoryMap.put(productFactory.getSupportedCategory().toLowerCase(Locale.ROOT), productFactory);
        }
    }

    public List<Product> getCustomerCatalogue(
        String search,
        String categoryId,
        String size,
        String colour,
        BigDecimal minPrice,
        BigDecimal maxPrice
    ) {
        ensureDefaultCategories();

        List<Product> products;
        if (hasText(categoryId)) {
            products = productRepository.findByCategory_CategoryID(categoryId.trim());
        } else if (hasText(search)) {
            products = productRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(search.trim(), search.trim());
        } else {
            products = productRepository.findByStatus(ACTIVE_STATUS);
        }

        return products.stream()
            .filter(product -> ACTIVE_STATUS.equalsIgnoreCase(product.getStatus()))
            .filter(product -> matchesSearch(product, search))
            .filter(product -> matchesCategory(product, categoryId))
            .filter(product -> matchesVariantFilters(product, size, colour))
            .filter(product -> matchesPriceRange(product, minPrice, maxPrice))
            .sorted(Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER))
            .toList();
    }

    public Product getCustomerProduct(String productId) {
        Product product = getProduct(productId);
        if (!ACTIVE_STATUS.equalsIgnoreCase(product.getStatus())) {
            throw new IllegalArgumentException("Product not found.");
        }
        return product;
    }

    public List<Product> getAdminCatalogue(String categoryId, String stockFilter, String statusFilter) {
        ensureDefaultCategories();

        List<Product> products = hasText(statusFilter)
            ? productRepository.findByStatus(normalizeStatus(statusFilter))
            : productRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));

        return products.stream()
            .filter(product -> matchesCategory(product, categoryId))
            .filter(product -> matchesStockFilter(product, stockFilter))
            .sorted(Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER))
            .toList();
    }

    public List<Category> getCategories() {
        ensureDefaultCategories();
        return categoryRepository.findAll(Sort.by(Sort.Direction.ASC, "categoryName"));
    }

    public List<String> getAvailableSizes() {
        List<String> sizes = new ArrayList<>(getActiveVariants().stream()
            .map(ProductVariant::getSize)
            .filter(ProductService::hasText)
            .map(String::trim)
            .collect(LinkedHashSet::new, Set::add, Set::addAll)
        );

        sizes.sort(String.CASE_INSENSITIVE_ORDER);
        return sizes;
    }

    public List<String> getAvailableColours() {
        List<String> colours = new ArrayList<>(getActiveVariants().stream()
            .map(ProductVariant::getColour)
            .filter(ProductService::hasText)
            .map(String::trim)
            .collect(LinkedHashSet::new, Set::add, Set::addAll)
        );

        colours.sort(String.CASE_INSENSITIVE_ORDER);
        return colours;
    }

    public ProductFormData createEmptyForm() {
        ProductFormData formData = new ProductFormData();
        formData.setStatus(ACTIVE_STATUS);
        formData.ensureAtLeastOneVariant();
        return formData;
    }

    public ProductFormData buildForm(String productId) {
        Product product = getProduct(productId);

        ProductFormData formData = new ProductFormData();
        formData.setName(product.getName());
        formData.setCategoryId(product.getCategory().getCategoryID());
        formData.setDescription(product.getDescription());
        formData.setBasePrice(product.getBasePrice());
        formData.setFabricType(product.getFabricType());
        formData.setImages(product.getImages());
        formData.setStatus(product.getStatus());

        List<ProductVariantFormData> variantForms = new ArrayList<>();
        for (ProductVariant variant : productVariantRepository.findByProduct_ProductID(productId)) {
            ProductVariantFormData variantFormData = new ProductVariantFormData();
            variantFormData.setVariantId(variant.getVariantID());
            variantFormData.setSize(variant.getSize());
            variantFormData.setColour(variant.getColour());
            variantFormData.setStockLevel(variant.getStockLevel());
            variantForms.add(variantFormData);
        }

        formData.setVariants(variantForms);
        formData.ensureAtLeastOneVariant();
        return formData;
    }

    public Product getProduct(String productId) {
        ensureDefaultCategories();
        return productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Product not found."));
    }

    @Transactional
    public Product createProduct(ProductFormData formData) {
        ValidatedProductData validatedData = validateProduct(formData, null);
        ProductFactory factory = validatedData.productFactory();

        Product product = factory.createProduct(
            validatedData.formData(),
            validatedData.category(),
            formatId("PRD", nextSequence(productRepository.findAll().stream().map(Product::getProductID).toList(), "PRD"))
        );

        product.setVariants(buildVariants(product, validatedData.formData().getVariants(), factory));
        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(String productId, ProductFormData formData) {
        Product product = getProduct(productId);
        ValidatedProductData validatedData = validateProduct(formData, productId);
        ProductFactory factory = validatedData.productFactory();

        factory.updateProduct(product, validatedData.formData(), validatedData.category());
        product.setVariants(buildVariants(product, validatedData.formData().getVariants(), factory));
        return productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(String productId) {
        Product product = getProduct(productId);
        productRepository.delete(product);
    }

    private void ensureDefaultCategories() {
        createCategoryIfMissing("CAT-TOPS", "Tops", "Upper-body fashion essentials and seasonal styles.");
        createCategoryIfMissing("CAT-BOTTOMS", "Bottoms", "Jeans, trousers, skirts, and other lower-body pieces.");
        createCategoryIfMissing("CAT-ACCESSORIES", "Accessories", "Bags, belts, jewellery, and add-on fashion items.");
    }

    private void createCategoryIfMissing(String categoryId, String categoryName, String description) {
        if (categoryRepository.findByCategoryName(categoryName) != null) {
            return;
        }

        Category category = new Category();
        category.setCategoryID(categoryId);
        category.setCategoryName(categoryName);
        category.setDescription(description);
        categoryRepository.save(category);
    }

    private ValidatedProductData validateProduct(ProductFormData originalFormData, String currentProductId) {
        ProductFormData sanitizedFormData = sanitizeForm(originalFormData);

        if (!hasText(sanitizedFormData.getName())) {
            throw new IllegalArgumentException("Product name is required.");
        }
        if (!hasText(sanitizedFormData.getCategoryId())) {
            throw new IllegalArgumentException("Category is required.");
        }
        if (!hasText(sanitizedFormData.getDescription())) {
            throw new IllegalArgumentException("Description is required.");
        }
        if (sanitizedFormData.getBasePrice() == null || sanitizedFormData.getBasePrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be a positive value.");
        }
        if (!hasText(sanitizedFormData.getFabricType())) {
            throw new IllegalArgumentException("Fabric type is required.");
        }

        Category category = categoryRepository.findById(sanitizedFormData.getCategoryId())
            .orElseThrow(() -> new IllegalArgumentException("Please choose a valid category."));
        ProductFactory productFactory = getFactory(category);

        List<ProductVariantFormData> variants = sanitizeVariants(sanitizedFormData.getVariants());
        sanitizedFormData.setVariants(variants);
        sanitizedFormData.setStatus(normalizeStatus(sanitizedFormData.getStatus()));

        boolean duplicateProduct = productRepository
            .findByNameIgnoreCaseAndCategory_CategoryID(sanitizedFormData.getName(), sanitizedFormData.getCategoryId())
            .stream()
            .anyMatch(product -> !product.getProductID().equals(currentProductId));

        if (duplicateProduct) {
            throw new IllegalArgumentException("A product with the same name already exists in this category.");
        }

        return new ValidatedProductData(sanitizedFormData, category, productFactory);
    }

    private ProductFormData sanitizeForm(ProductFormData originalFormData) {
        ProductFormData sanitizedFormData = new ProductFormData();
        sanitizedFormData.setName(trimToNull(originalFormData.getName()));
        sanitizedFormData.setCategoryId(trimToNull(originalFormData.getCategoryId()));
        sanitizedFormData.setDescription(trimToNull(originalFormData.getDescription()));
        sanitizedFormData.setBasePrice(originalFormData.getBasePrice());
        sanitizedFormData.setFabricType(trimToNull(originalFormData.getFabricType()));
        sanitizedFormData.setImages(normalizeImages(originalFormData.getImages()));
        sanitizedFormData.setStatus(trimToNull(originalFormData.getStatus()));
        sanitizedFormData.setVariants(originalFormData.getVariants());
        return sanitizedFormData;
    }

    private List<ProductVariantFormData> sanitizeVariants(List<ProductVariantFormData> variants) {
        List<ProductVariantFormData> sanitizedVariants = new ArrayList<>();
        Set<String> uniqueVariants = new HashSet<>();

        if (variants == null) {
            throw new IllegalArgumentException("At least one size and colour variant is required.");
        }

        for (ProductVariantFormData variant : variants) {
            boolean blankSize = !hasText(variant.getSize());
            boolean blankColour = !hasText(variant.getColour());
            boolean blankStock = variant.getStockLevel() == null;

            if (blankSize && blankColour && blankStock) {
                continue;
            }

            if (blankSize || blankColour || blankStock) {
                throw new IllegalArgumentException("Every variant row must include size, colour, and stock.");
            }

            if (variant.getStockLevel() < 0) {
                throw new IllegalArgumentException("Stock availability must be a non-negative value.");
            }

            ProductVariantFormData sanitizedVariant = new ProductVariantFormData();
            sanitizedVariant.setVariantId(trimToNull(variant.getVariantId()));
            sanitizedVariant.setSize(variant.getSize().trim().toUpperCase(Locale.ROOT));
            sanitizedVariant.setColour(variant.getColour().trim());
            sanitizedVariant.setStockLevel(variant.getStockLevel());

            String uniquenessKey = sanitizedVariant.getSize() + "|" + sanitizedVariant.getColour().toLowerCase(Locale.ROOT);
            if (!uniqueVariants.add(uniquenessKey)) {
                throw new IllegalArgumentException("Duplicate size and colour combinations are not allowed.");
            }

            sanitizedVariants.add(sanitizedVariant);
        }

        if (sanitizedVariants.isEmpty()) {
            throw new IllegalArgumentException("At least one size and colour variant is required.");
        }

        return sanitizedVariants;
    }

    private List<ProductVariant> buildVariants(
        Product product,
        List<ProductVariantFormData> variants,
        ProductFactory factory
    ) {
        Map<String, ProductVariant> existingVariants = new HashMap<>();
        for (ProductVariant existingVariant : product.getVariants()) {
            existingVariants.put(existingVariant.getVariantID(), existingVariant);
        }

        int nextVariantSequence = nextSequence(
            productVariantRepository.findAll().stream().map(ProductVariant::getVariantID).toList(),
            "VAR"
        );

        List<ProductVariant> productVariants = new ArrayList<>();
        for (ProductVariantFormData variant : variants) {
            if (hasText(variant.getVariantId()) && existingVariants.containsKey(variant.getVariantId().trim())) {
                ProductVariant existingVariant = existingVariants.get(variant.getVariantId().trim());
                existingVariant.setProduct(product);
                existingVariant.setSize(variant.getSize());
                existingVariant.setColour(variant.getColour());
                existingVariant.setStockLevel(variant.getStockLevel());
                productVariants.add(existingVariant);
                continue;
            }

            String variantId = hasText(variant.getVariantId()) ? variant.getVariantId().trim() : formatId("VAR", nextVariantSequence++);
            productVariants.add(factory.createVariant(product, variant, variantId));
        }

        return productVariants;
    }

    private ProductFactory getFactory(Category category) {
        ProductFactory productFactory = factoryMap.get(category.getCategoryName().toLowerCase(Locale.ROOT));
        if (productFactory == null) {
            throw new IllegalArgumentException("No factory is configured for category " + category.getCategoryName() + ".");
        }
        return productFactory;
    }

    private List<ProductVariant> getActiveVariants() {
        return productRepository.findByStatus(ACTIVE_STATUS).stream()
            .flatMap(product -> product.getVariants().stream())
            .filter(ProductVariant::isAvailable)
            .toList();
    }

    private boolean matchesSearch(Product product, String search) {
        if (!hasText(search)) {
            return true;
        }

        String keyword = search.trim().toLowerCase(Locale.ROOT);
        String description = product.getDescription() == null ? "" : product.getDescription();

        return product.getName().toLowerCase(Locale.ROOT).contains(keyword)
            || description.toLowerCase(Locale.ROOT).contains(keyword);
    }

    private boolean matchesCategory(Product product, String categoryId) {
        return !hasText(categoryId)
            || product.getCategory().getCategoryID().equalsIgnoreCase(categoryId.trim());
    }

    private boolean matchesVariantFilters(Product product, String size, String colour) {
        boolean requireSize = hasText(size);
        boolean requireColour = hasText(colour);
        if (!requireSize && !requireColour) {
            return true;
        }

        return product.getVariants().stream().anyMatch(variant -> {
            boolean sizeMatch = !requireSize || variant.getSize().equalsIgnoreCase(size.trim());
            boolean colourMatch = !requireColour || variant.getColour().equalsIgnoreCase(colour.trim());
            return sizeMatch && colourMatch && variant.getStockLevel() >= 0;
        });
    }

    private boolean matchesPriceRange(Product product, BigDecimal minPrice, BigDecimal maxPrice) {
        if (minPrice != null && product.getBasePrice().compareTo(minPrice) < 0) {
            return false;
        }
        if (maxPrice != null && product.getBasePrice().compareTo(maxPrice) > 0) {
            return false;
        }
        return true;
    }

    private boolean matchesStockFilter(Product product, String stockFilter) {
        if (!hasText(stockFilter)) {
            return true;
        }

        return switch (stockFilter.trim().toLowerCase(Locale.ROOT)) {
            case "in-stock" -> product.isInStock();
            case "out-of-stock" -> !product.isInStock();
            default -> true;
        };
    }

    private String normalizeStatus(String status) {
        return INACTIVE_STATUS.equalsIgnoreCase(status) ? INACTIVE_STATUS : ACTIVE_STATUS;
    }

    private String normalizeImages(String images) {
        if (!hasText(images)) {
            return null;
        }

        List<String> normalizedPaths = new ArrayList<>();
        for (String image : images.split(",")) {
            if (hasText(image)) {
                normalizedPaths.add(image.trim());
            }
        }

        return normalizedPaths.isEmpty() ? null : String.join(", ", normalizedPaths);
    }

    private int nextSequence(List<String> existingIds, String prefix) {
        int maxValue = 0;

        for (String id : existingIds) {
            if (!hasText(id) || !id.startsWith(prefix)) {
                continue;
            }

            String numericPart = id.substring(prefix.length()).replaceAll("[^0-9]", "");
            if (!numericPart.isBlank()) {
                maxValue = Math.max(maxValue, Integer.parseInt(numericPart));
            }
        }

        return maxValue + 1;
    }

    private String formatId(String prefix, int value) {
        return prefix + String.format("%03d", value);
    }

    private static boolean hasText(String value) {
        return value != null && !value.trim().isBlank();
    }

    private String trimToNull(String value) {
        return hasText(value) ? value.trim() : null;
    }

    private record ValidatedProductData(
        ProductFormData formData,
        Category category,
        ProductFactory productFactory
    ) {}

    public static class ProductFormData {
        private String name;
        private String categoryId;
        private String description;
        private BigDecimal basePrice;
        private String fabricType;
        private String images;
        private String status;
        private List<ProductVariantFormData> variants = new ArrayList<>();

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getCategoryId() { return categoryId; }
        public void setCategoryId(String categoryId) { this.categoryId = categoryId; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public BigDecimal getBasePrice() { return basePrice; }
        public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }

        public String getFabricType() { return fabricType; }
        public void setFabricType(String fabricType) { this.fabricType = fabricType; }

        public String getImages() { return images; }
        public void setImages(String images) { this.images = images; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public List<ProductVariantFormData> getVariants() { return variants; }

        public void setVariants(List<ProductVariantFormData> variants) {
            this.variants = variants == null ? new ArrayList<>() : variants;
        }

        public void ensureAtLeastOneVariant() {
            if (variants == null) {
                variants = new ArrayList<>();
            }
            if (variants.isEmpty()) {
                variants.add(new ProductVariantFormData());
            }
        }
    }

    public static class ProductVariantFormData {
        private String variantId;
        private String size;
        private String colour;
        private Integer stockLevel;

        public String getVariantId() { return variantId; }
        public void setVariantId(String variantId) { this.variantId = variantId; }

        public String getSize() { return size; }
        public void setSize(String size) { this.size = size; }

        public String getColour() { return colour; }
        public void setColour(String colour) { this.colour = colour; }

        public Integer getStockLevel() { return stockLevel; }
        public void setStockLevel(Integer stockLevel) { this.stockLevel = stockLevel; }
    }
}