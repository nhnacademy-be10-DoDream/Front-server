const selectedCouponIds = new Set();
let pointPolicy = {
    basePoint: 0,
    rate: 0
};


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
        const index = quantityInput.getAttribute("data-index");

        const quantityHiddenInput = document.querySelector(`input[name='items[${index}].quantity']`);
        if (quantityHiddenInput) {
            quantityHiddenInput.value = quantity;
        }

        const priceSpan = row.querySelector('[id^="priceDisplay-"]');
        const salePrice = parseInt(priceSpan?.dataset.originalPrice || 0);
        const finalPriceText = priceSpan?.innerText || '0';
        const finalPrice = parseInt(finalPriceText.replace(/[^0-9]/g, ''));

        totalProductPrice += salePrice * quantity;
        totalDiscount += (salePrice - finalPrice) * quantity;

        const wrappingSelect = row.querySelector('.wrapping-select');
        const selectedOption = wrappingSelect?.selectedOptions[0];
        const wrappingPrice = selectedOption ? parseInt(selectedOption.dataset.price || 0) : 0;
        totalWrapping += wrappingPrice * quantity;

        const wrappingHiddenInput = document.querySelector(`input[name='items[${index}].wrappingId']`);
        if(wrappingHiddenInput) {
            wrappingHiddenInput.value = selectedOption?.value || "";
        }
    });

    const discountedPrice = totalProductPrice - totalDiscount;
    const orderTotal = discountedPrice + totalWrapping;

    const rewardPoint = Math.floor((orderTotal * pointPolicy.rate) + pointPolicy.basePoint);

    document.querySelector("input[name='orderTotal']").value = orderTotal;
    document.querySelector("input[name='totalProductPrice']").value = totalProductPrice;
    document.querySelector("input[name='totalDiscount']").value = totalDiscount;
    document.querySelector("input[name='totalWrappingPrice']").value = totalWrapping;

    document.getElementById("totalProductPrice").innerText = formatPrice(totalProductPrice);
    document.getElementById("totalDiscount").innerText = formatPrice(-totalDiscount);
    document.getElementById("totalWrappingPrice").innerText = formatPrice(totalWrapping);
    document.getElementById("orderTotal").innerText = formatPrice(orderTotal);
    document.getElementById("rewardPoint").innerText = formatPrice(rewardPoint);
}

function updateQuantity(button) {
    const cartItemId = button.dataset.cartItemId;
    const quantityInput = button.previousElementSibling;
    const newQuantity = quantityInput.value;

    if (newQuantity < 1) {
        alert("수량은 1 이상이어야 합니다.");
        return;
    }

    fetch(`/cart/${cartItemId}?quantity=${newQuantity}`, {
        method: 'PUT',
        headers: {
            'Accept': 'application/json'
        }
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('수량 변경에 실패했습니다. 다시 시도해주세요.');
            }
            console.log('수량이 성공적으로 변경되었습니다.');
            recalculateTotal();
        })
        .catch(error => {
            console.error('Error:', error);
            alert(error.message);
        });
}

function openCouponModal(button) {
    const cartItemId = button.dataset.cartItemId;
    const bookId = button.dataset.bookId;
    const couponListDiv = document.getElementById("couponList");

    const appliedCouponInput = document.getElementById('selectedCouponId-' + cartItemId);
    if (appliedCouponInput && appliedCouponInput.value) {
        selectedCouponIds.delete(parseInt(appliedCouponInput.value));
    }

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
                    couponDiv.className = "border rounded p-3 mb-2";

                    const discountText = coupon.discountValue <= 100 ?
                        `${coupon.discountValue}%` :
                        `${coupon.discountValue.toLocaleString()}원`;

                    const safePolicyName = coupon.policyName.replace(/'/g, "\\'");

                    couponDiv.innerHTML = `
                        <h6>${coupon.policyName}</h6>
                        <small>
                            <strong>${coupon.discountValue <= 100 ? '할인율' : '할인금액'}:</strong> ${discountText}<br>
                            <strong>최소 구매 금액:</strong> ${coupon.minPurchaseAmount.toLocaleString()}원<br>
                            <strong>최대 할인 금액:</strong> ${coupon.maxDiscountAmount.toLocaleString()}원
                        </small>
                        <button class="btn btn-sm btn-outline-success mt-2 w-100" 
                                onclick="selectCoupon(${cartItemId}, ${coupon.couponId}, ${coupon.finalPrice}, '${safePolicyName}')">
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
            couponListDiv.innerHTML = `<p class="text-danger">쿠폰을 불러오는데 실패했습니다: ${error.message}</p>`;
        });
}

function selectCoupon(cartItemId, couponId, finalPrice, policyName) {

    if (selectedCouponIds.has(couponId)) {
        alert("이미 다른 상품에 적용된 쿠폰입니다.");
        return;
    }

    const oldCouponInput = document.getElementById("selectedCouponId-" + cartItemId);
    if (oldCouponInput && oldCouponInput.value) {
        selectedCouponIds.delete(parseInt(oldCouponInput.value));
    }

    selectedCouponIds.add(couponId);

    const priceSpan = document.getElementById("priceDisplay-" + cartItemId);
    if (priceSpan) {
        priceSpan.innerText = formatPrice(finalPrice);
    }

    const cartItemRow = document.querySelector(`tr[data-sale-price] [id='priceDisplay-${cartItemId}']`)?.closest("tr");
    if (cartItemRow) {
        const index = cartItemRow.querySelector(".quantity-input")?.getAttribute("data-index");
        document.getElementById(`couponId-${index}`).value = couponId;
        document.getElementById(`finalPrice-${index}`).value = finalPrice;
    }

    const couponCell = document.getElementById('coupon-cell-' + cartItemId);
    const originalPrice = document.getElementById('priceDisplay-' + cartItemId).dataset.originalPrice;
    if (couponCell) {
        couponCell.innerHTML = `
            <div class="d-flex align-items-center justify-content-center flex-column flex-sm-row gap-2">
                <span class="badge bg-success text-white text-truncate" style="max-width: 150px;" title="${policyName}">${policyName}</span>
                <button type="button" class="btn btn-xs btn-outline-danger" onclick="cancelCoupon(${cartItemId}, ${originalPrice})">취소</button>
            </div>
            <input type="hidden" id="selectedCouponId-${cartItemId}" value="${couponId}" />
        `;
    }

    closeCouponModal();
    recalculateTotal();
}

function cancelCoupon(cartItemId, originalPrice) {
    const couponIdInput = document.getElementById("selectedCouponId-" + cartItemId);
    if (couponIdInput && couponIdInput.value) {
        selectedCouponIds.delete(parseInt(couponIdInput.value));
    }

    const priceSpan = document.getElementById("priceDisplay-" + cartItemId);
    if (priceSpan) {
        priceSpan.innerText = formatPrice(originalPrice);
    }

    const cartItemRow = document.querySelector(`tr[data-sale-price] [id='priceDisplay-${cartItemId}']`)?.closest("tr");
    if (cartItemRow) {
        const index = cartItemRow.querySelector(".quantity-input")?.getAttribute("data-index");
        document.getElementById(`couponId-${index}`).value = "";
        document.getElementById(`finalPrice-${index}`).value = ""; // 최종 가격도 원래 판매가로 되돌려야 함
    }

    const couponCell = document.getElementById('coupon-cell-' + cartItemId);
    if (couponCell) {
        const bookId = cartItemRow.querySelector('a[href*="/books/"]').href.split('/').pop();
        const salePrice = priceSpan.dataset.originalPrice;

        couponCell.innerHTML = `
            <button type="button" class="btn btn-sm btn-outline-primary"
                    data-cart-item-id="${cartItemId}" 
                    data-book-id="${bookId}" 
                    data-sale-price="${salePrice}"
                    onclick="openCouponModal(this)">쿠폰 선택</button>
            <input type="hidden" id="selectedCouponId-${cartItemId}" value="" />
        `;
    }

    recalculateTotal();
}

function closeCouponModal() {
    document.getElementById("couponModal").style.display = "none";
}

function getSelectedCoupons() {
    const result = [];
    document.querySelectorAll("input[id^='selectedCouponId-']").forEach(input => {
        if (input.value) {
            const cartItemId = input.id.replace('selectedCouponId-', '');
            const row = document.getElementById('coupon-cell-' + cartItemId)?.closest('tr');
            if(row) {
                const bookLink = row.querySelector('a[href*="/books/"]');
                const bookIdMatch = bookLink?.getAttribute("href")?.match(/\/books\/(\d+)/);
                if (bookIdMatch) {
                    result.push({
                        bookId: parseInt(bookIdMatch[1]),
                        userCouponId: parseInt(input.value)
                    });
                }
            }
        }
    });
    return result;
}

function submitOrder() {
    const cartItemRows = document.querySelectorAll('tr[data-sale-price]');
    if (cartItemRows.length === 0) {
        alert("장바구니에 담긴 책이 없습니다. 상품을 추가해주세요.");
        return;
    }

    recalculateTotal();

    const selectedCoupons = getSelectedCoupons();
    const orderForm = document.getElementById("orderForm");

    if (selectedCoupons.length === 0) {
        orderForm.submit();
        return;
    }

    fetch("/cart/coupons/multiple", {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ requests: selectedCoupons })
    })
        .then(response => {
            if (!response.ok) throw new Error("쿠폰 사용 처리에 실패했습니다.");
            orderForm.submit();
        })
        .catch(error => {
            alert("주문 처리 중 오류가 발생했습니다: " + error.message);
            console.error(error);
        });
}

window.onload = () => {

    document.querySelectorAll('[id^="priceDisplay-"]').forEach(span => {
        const rawPrice = span.textContent.replace(/[^0-9]/g, '');
        span.dataset.originalPrice = rawPrice;
    });


    fetch("/cart/point-rate")
        .then(res => res.json())
        .then(data => {
            pointPolicy.basePoint = data.basePoint || 0;
            pointPolicy.rate = data.rate || 0; // 예: 0.05 (5%)
        })
        .catch(err => {
            console.error("적립 정책 로딩 실패:", err);
        })
        .finally(() => {
            recalculateTotal();
        });

    document.querySelectorAll('.quantity-input').forEach(input => {
        input.addEventListener('change', recalculateTotal);
    });
};
