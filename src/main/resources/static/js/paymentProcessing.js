const urlParams = new URLSearchParams(window.location.search);

async function confirm() {
    const requestData = {
        paymentKey: urlParams.get("paymentKey"),
        orderId: urlParams.get("orderId"),
        amount: urlParams.get("amount")
    };

    const headers = {
        "Content-Type": "application/json",
        "X-PAYMENT-PROVIDER": urlParams.get("provider") || "TOSS"
    };

    if (userId) {
        headers["X-USER-ID"] = userId;
    }

    const response = await fetch("/payment/confirm", {
        method: "POST",
        headers,
        body: JSON.stringify(requestData)
    });

    const json = await response.json();

    if (!response.ok) {
        window.location.href = `/payment/fail?errorCode=${json.code}&errorMessage=${encodeURIComponent(json.message)}`;
        return;
    }

    const query = new URLSearchParams({
        orderId: requestData.orderId,
        amount: requestData.amount,
        method: json.method || json.easyPay?.provider || "N/A",
        address: json.shippingAddress || "배송지 정보 없음",
        items: (json.items || []).map(i => `${i.title}(${i.quantity})`).join(';')
    });

    window.location.href = `/payment/success?${query.toString()}`;
}

confirm();