
    function formatPrice(value) {
    return value.toLocaleString() + '원';
}

    function recalculateGuestCartTotal() {
    let totalProductPrice = 0;
    let totalWrappingPrice = 0;
    const seenBookIds = new Set();

    document.querySelectorAll("tbody tr").forEach(row => {
    const priceSpan = row.querySelector("td:nth-child(2) span");
    const salePriceText = priceSpan ? priceSpan.textContent : '0';
    const salePrice = Number(salePriceText.replace(/[^0-9]/g, "")) || 0;

    const quantityInput = row.querySelector("input[type='number']");
    let quantity = 1;
    if (quantityInput && quantityInput.value !== "") {
    quantity = Number(quantityInput.value);
    if (isNaN(quantity) || quantity < 1) quantity = 1;
}

    // bookId 추출 (상품명 td의 a 태그에서 id 파싱)
    const bookLink = row.querySelector("td:nth-child(1) a");
    let bookId = null;
    if (bookLink) {
    const href = bookLink.getAttribute('href');
    const match = href.match(/\/books\/(\d+)/);
    if (match) bookId = match[1];
}

    // 0원 행, 중복 bookId 행 제외
    if (salePrice > 0 && bookId && !seenBookIds.has(bookId)) {
    seenBookIds.add(bookId);
    totalProductPrice += salePrice * quantity;

    const wrappingSelect = row.querySelector('.wrapping-select');
    const selectedOption = wrappingSelect?.selectedOptions[0];
    const wrappingPrice = selectedOption ? Number(selectedOption.dataset.price || 0) : 0;
    totalWrappingPrice += wrappingPrice * quantity;

    const index = wrappingSelect.getAttribute("data-index");
    document.querySelector(`input[name='items[${index}].wrappingId']`).value = selectedOption?.value || "";
}
});

    const orderTotal = totalProductPrice + totalWrappingPrice;
    const orderTotalInput = document.querySelector("input[name='orderTotal']");
    if (orderTotalInput) {
    orderTotalInput.value = orderTotal; // 값 반영
}
    document.getElementById("totalProductPrice").innerText = formatPrice(totalProductPrice);
    document.getElementById("orderTotal").innerText = formatPrice(orderTotal);
    document.getElementById("totalWrappingPrice").innerText = formatPrice(totalWrappingPrice)
}


    window.onload = () => {

    recalculateGuestCartTotal();
    document.querySelectorAll("select").forEach(select => {
    select.addEventListener("change", recalculateGuestCartTotal);
});

};
