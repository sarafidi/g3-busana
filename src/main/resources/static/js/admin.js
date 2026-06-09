document.addEventListener("DOMContentLoaded", () => {
    const variantEditor = document.querySelector("[data-variant-editor]");
    const addVariantButton = document.querySelector("[data-add-variant]");
    const template = document.querySelector("#variant-row-template");

    if (!variantEditor || !addVariantButton || !template) {
        return;
    }

    const bindRemoveButtons = () => {
        variantEditor.querySelectorAll("[data-remove-variant]").forEach((button) => {
            button.onclick = () => {
                const rows = variantEditor.querySelectorAll(".variant-editor-row");
                const row = button.closest(".variant-editor-row");

                if (!row) {
                    return;
                }

                if (rows.length === 1) {
                    row.querySelectorAll("input").forEach((input) => {
                        input.value = "";
                    });
                    return;
                }

                row.remove();
            };
        });
    };

    addVariantButton.addEventListener("click", () => {
        const nextIndex = Number(variantEditor.dataset.nextIndex || "0");
        const fragmentMarkup = template.innerHTML.replaceAll("__INDEX__", String(nextIndex));
        variantEditor.insertAdjacentHTML("beforeend", fragmentMarkup);
        variantEditor.dataset.nextIndex = String(nextIndex + 1);
        bindRemoveButtons();
    });

    bindRemoveButtons();
});
