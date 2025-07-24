function formatPrice(value) {
    return value.toLocaleString() + '원';
}

function recalculateGuestCartTotal() {
    let totalProductPrice = 0;
    let totalWrappingPrice = 0;

    document.querySelectorAll("tbody tr").forEach(row => {

        const priceCell = row.querySelector("td:nth-child(2)");
        const salePriceText = priceCell ? priceCell.textContent : '0';
        const salePrice = Number(salePriceText.replace(/[^0-9]/g, "")) || 0;

        const quantityInput = row.querySelector("input[type='number']");
        let quantity = 1;
        if (quantityInput && quantityInput.value !== "") {
            quantity = Number(quantityInput.value);
            if (isNaN(quantity) || quantity < 1) quantity = 1;
        }

        if (salePrice > 0) {
            totalProductPrice += salePrice * quantity;

            const wrappingSelect = row.querySelector('.wrapping-select');
            const selectedOption = wrappingSelect?.selectedOptions[0];
            const wrappingPrice = selectedOption ? Number(selectedOption.dataset.price || 0) : 0;
            totalWrappingPrice += wrappingPrice * quantity;

            const index = wrappingSelect.getAttribute("data-index");
            const wrappingIdInput = document.querySelector(`input[name='items[${index}].wrappingId']`);
            const quantityInput = document.querySelector(`input[name='items[${index}].quantity']`);
            if (wrappingIdInput) {
                wrappingIdInput.value = selectedOption?.value || "";
            }
            if (quantityInput) {
                quantityInput.value = quantity;
            }
        }
    });

    const orderTotal = totalProductPrice + totalWrappingPrice;

    document.querySelector("input[name='totalProductPrice']").value = totalProductPrice;
    document.querySelector("input[name='totalWrappingPrice']").value = totalWrappingPrice;

    document.getElementById("totalProductPrice").innerText = formatPrice(totalProductPrice);
    document.getElementById("totalWrappingPrice").innerText = formatPrice(totalWrappingPrice);
    document.getElementById("orderTotal").innerText = formatPrice(orderTotal);
}

function submitGuestOrder() {

    const cartItemRows = document.querySelectorAll("tbody tr");
    if (cartItemRows.length === 0) {
        alert("장바구니에 담긴 책이 없습니다. 상품을 추가해주세요.");
        return;
    }

    recalculateGuestCartTotal();


    const orderForm = document.getElementById("guestOrderForm");
    if (orderForm) {
        orderForm.submit();
    }
}

window.onload = () => {
    recalculateGuestCartTotal();

    document.querySelectorAll(".wrapping-select, input[type='number']").forEach(element => {
        element.addEventListener("change", recalculateGuestCartTotal);
    });
};