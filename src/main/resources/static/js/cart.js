
    const selectedCouponIds = new Set();

    function formatPrice(val) {
    return val.toLocaleString() + '원';
}

    function recalculateTotal() {
    let totalProductPrice = 0;
    let totalDiscount = 0;
    let totalWrapping = 0;

    document.querySelectorAll('tr[data-sale-price]').forEach(row => {
    const quantityInput = row.querySelector('.quantity-input');
    const quantity = parseInt(quantityInput?.value || 1);

    const priceSpan = row.querySelector('[id^="priceDisplay-"]');
    const salePrice = parseInt(priceSpan?.dataset.originalPrice || 0);
    const finalPrice = parseInt(priceSpan?.innerText.replace(/[^0-9]/g, '') || 0);

    totalProductPrice += salePrice * quantity;
    totalDiscount += (salePrice - finalPrice) * quantity;

    const wrappingSelect = row.querySelector('.wrapping-select');
    const selectedOption = wrappingSelect?.selectedOptions[0];
    const wrappingPrice = selectedOption ? parseInt(selectedOption.dataset.price || 0) : 0;
    totalWrapping += wrappingPrice * quantity;

    const index = wrappingSelect.getAttribute("data-index");
    document.querySelector(`input[name='items[${index}].wrappingId']`).value = selectedOption?.value || "";
});

    const discountedPrice = totalProductPrice - totalDiscount;
    const orderTotal = discountedPrice + totalWrapping;
    const rewardPoint = Math.floor(orderTotal * 0.1);

    document.querySelector("input[name='orderTotal']").value = orderTotal;
    document.querySelector("input[name='totalProductPrice']").value = totalProductPrice;
    document.querySelector("input[name='totalDiscount']").value = totalDiscount;
    document.getElementById("totalProductPrice").innerText = formatPrice(totalProductPrice);
    document.getElementById("totalDiscount").innerText = formatPrice(-totalDiscount);
    document.getElementById("totalWrappingPrice").innerText = formatPrice(totalWrapping);
    document.getElementById("orderTotal").innerText = formatPrice(orderTotal);
    document.getElementById("rewardPoint").innerText = formatPrice(rewardPoint);
}

    function openCouponModal(button) {
    const cartItemId = button.dataset.cartItemId;
    const bookId = button.dataset.bookId;
    const couponListDiv = document.getElementById("couponList");
    couponListDiv.innerHTML = "<p>로딩 중...</p>";

    document.getElementById("couponModal").style.display = "block";

    fetch(`/cart/coupons?bookId=${bookId}`)
    .then(response => {
    if (!response.ok) throw new Error("쿠폰 불러오기 실패");
    return response.json();
})
    .then(coupons => {
    couponListDiv.innerHTML = "";

    const filteredCoupons = coupons.filter(c => !selectedCouponIds.has(c.couponId));

    if (filteredCoupons.length > 0) {
    filteredCoupons.forEach(coupon => {
    const couponDiv = document.createElement("div");
    couponDiv.style.border = "1px solid #ccc";
    couponDiv.style.padding = "10px";
    couponDiv.style.marginBottom = "10px";

    const discountText = coupon.discountValue <= 100
    ? `${coupon.discountValue}%`
    : `${coupon.discountValue.toLocaleString()}원`;

    couponDiv.innerHTML = `
                            <strong>쿠폰 이름:</strong> ${coupon.policyName}<br>
                            <strong>${coupon.discountValue <= 100 ? '할인율' : '할인금액'}:</strong> ${discountText}<br>
                            <strong>최소 구매 금액:</strong> ${coupon.minPurchaseAmount.toLocaleString()}원<br>
                            <strong>최대 할인 금액:</strong> ${coupon.maxDiscountAmount.toLocaleString()}원<br>
                            <button class="btn btn-sm btn-outline-success mt-2" onclick="selectCoupon(${cartItemId}, ${coupon.couponId}, ${coupon.finalPrice})">
                            <i class="bi bi-tag"></i> 쿠폰 적용
                            </button>
                        `;
    couponListDiv.appendChild(couponDiv);
});
} else {
    couponListDiv.innerHTML = "<p>사용 가능한 쿠폰이 없습니다.</p>";
}
})
    .catch(error => {
    couponListDiv.innerHTML = "<p>쿠폰을 불러오는데 실패했습니다.</p>";
});
}

    function selectCoupon(cartItemId, couponId, finalPrice) {
    // 기존 같은 쿠폰이 다른 항목에 적용되어 있다면 제거
    document.querySelectorAll("input[id^='selectedCouponId-']").forEach(input => {
        if (input.value === String(couponId)) {
            input.value = ""; // 해당 input 비움
        }
    });


    // 현재 항목에 쿠폰 적용
    document.getElementById("selectedCouponId-" + cartItemId).value = couponId;
    selectedCouponIds.add(couponId);

    const priceSpan = document.getElementById("priceDisplay-" + cartItemId);
    if (priceSpan) {
    priceSpan.innerText = finalPrice.toLocaleString() + "원";
}

    // 히든 인풋 채우기
    const cartItemRow = document.querySelector(`tr[data-sale-price] [id='priceDisplay-${cartItemId}']`)?.closest("tr");
    if (cartItemRow) {
    const index = cartItemRow.querySelector(".wrapping-select")?.getAttribute("data-index");

    const couponInput = document.getElementById(`couponId-${index}`);
    const finalPriceInput = document.getElementById(`finalPrice-${index}`);
    if (couponInput) couponInput.value = couponId;
    if (finalPriceInput) finalPriceInput.value = finalPrice;
}

    closeCouponModal();
    recalculateTotal();
}

    function closeCouponModal() {
    document.getElementById("couponModal").style.display = "none";
}

    function getSelectedCoupons() {
    const result = [];

    document.querySelectorAll("tr[data-sale-price]").forEach(row => {
    const link = row.querySelector("a");
    const bookIdMatch = link?.getAttribute("href")?.match(/\/books\/(\d+)/);
    const bookId = bookIdMatch ? parseInt(bookIdMatch[1]) : null;

    const couponInput = row.querySelector("input[id^='selectedCouponId-']");
    let userCouponId = null;

    if (couponInput && couponInput.value.trim() !== "") {
    const parsed = parseInt(couponInput.value);
    if (!isNaN(parsed)) {
    userCouponId = parsed;
}
}

    if (bookId !== null && userCouponId !== null) {
    result.push({
    bookId,
    userCouponId
});
}
});

    return result;
}


    function submitOrder() {
    const selectedCoupons = getSelectedCoupons();

    if (selectedCoupons.length === 0) {
    document.getElementById("orderForm").submit();
    return;
}

    fetch("/cart/coupons/multiple", {
    method: "PUT",
    headers: {
    "Content-Type": "application/json"
},
    body: JSON.stringify({
    requests: selectedCoupons.map(c => ({
    bookId: c.bookId,
    userCouponId: c.userCouponId
}))
})
}).then(response => {
    if (!response.ok) throw new Error("쿠폰 사용 실패");
    document.getElementById("orderForm").submit();
}).catch(error => {
    alert("쿠폰 사용 중 오류가 발생했습니다.");
    console.error(error);
});
}

    window.onload = () => {
    document.querySelectorAll('[id^="priceDisplay-"]').forEach(span => {
        const rawPrice = span.textContent.replace(/[^0-9]/g, '');
        span.dataset.originalPrice = rawPrice;
    });
    recalculateTotal();
};
