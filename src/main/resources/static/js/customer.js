document.addEventListener("DOMContentLoaded", () => {
    const picker = document.querySelector("[data-variant-picker]");
    const selectedVariant = document.querySelector("[data-selected-variant]");
    const selectedStock = document.querySelector("[data-selected-stock]");

    if (!picker || !selectedVariant || !selectedStock) {
        return;
    }

    const inputs = Array.from(picker.querySelectorAll("input[type='radio']"));
    const enabledInput = inputs.find((input) => !input.disabled);

    if (!inputs.some((input) => input.checked) && enabledInput) {
        enabledInput.checked = true;
    }

    const updateSelection = () => {
        const activeInput = inputs.find((input) => input.checked);
        if (!activeInput) {
            selectedVariant.textContent = "Choose an available option";
            selectedStock.textContent = "Stock visibility updates here.";
            return;
        }

        const variantId = activeInput.value;
        const stockLevel = parseInt(activeInput.dataset.stockLevel || "0", 10);

        selectedVariant.textContent = activeInput.dataset.variantLabel || "Selected variant";
        selectedStock.textContent = `Stock available: ${stockLevel}`;

        // Sync with hidden input fields for form submission
        const cartVariantInput = document.getElementById("cartVariantID");
        const wishlistVariantInput = document.getElementById("wishlistVariantID");
        const addToCartBtn = document.getElementById("addToCartBtn");
        const quantityInput = document.getElementById("quantity");

        if (cartVariantInput) cartVariantInput.value = variantId;
        if (wishlistVariantInput) wishlistVariantInput.value = variantId;

        if (addToCartBtn) {
            if (stockLevel <= 0) {
                addToCartBtn.disabled = true;
                addToCartBtn.textContent = "Out of Stock";
                addToCartBtn.classList.add("bg-stone-300", "cursor-not-allowed");
                addToCartBtn.classList.remove("bg-[#b4572d]", "hover:bg-[#8f3c1c]");
            } else {
                addToCartBtn.disabled = false;
                addToCartBtn.textContent = "Add to Cart";
                addToCartBtn.classList.remove("bg-stone-300", "cursor-not-allowed");
                addToCartBtn.classList.add("bg-[#b4572d]", "hover:bg-[#8f3c1c]");
            }
        }

        if (quantityInput) {
            quantityInput.max = stockLevel;
            if (parseInt(quantityInput.value, 10) > stockLevel) {
                quantityInput.value = stockLevel;
            }
        }
    };

    inputs.forEach((input) => input.addEventListener("change", updateSelection));
    updateSelection();
});
