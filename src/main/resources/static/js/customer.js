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

        selectedVariant.textContent = activeInput.dataset.variantLabel || "Selected variant";
        selectedStock.textContent = `Stock available: ${activeInput.dataset.stockLevel || "0"}`;
    };

    inputs.forEach((input) => input.addEventListener("change", updateSelection));
    updateSelection();
});
