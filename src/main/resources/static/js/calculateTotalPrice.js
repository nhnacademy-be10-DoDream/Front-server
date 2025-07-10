function updateTotal() {
    const MIN_PAYMENT_AMOUNT = 1000;
    let pointInput = document.getElementById("pointUsed");
    let pointUsed = parseInt(pointInput.value) || 0;
    let shippingFee = 0;

    const selectedOption = document.querySelector("#shippingPolicyId option:checked");
    if (selectedOption && selectedOption.dataset.fee) {
        shippingFee = parseInt(selectedOption.dataset.fee);
    }

    const rawTotal = window.baseAmount + shippingFee;
    const maxUsablePoint = Math.min(window.maxPoint, rawTotal - MIN_PAYMENT_AMOUNT);

    if (pointUsed > maxUsablePoint) {
        pointUsed = maxUsablePoint > 0 ? maxUsablePoint : 0;
        pointInput.value = pointUsed;
    }

    let finalAmount = rawTotal - pointUsed;
    document.getElementById("finalTotal").innerText = finalAmount.toLocaleString() + '원';
    document.getElementById("totalPrice").value = finalAmount;
}

function useAllPoints() {
    const selectedOption = document.querySelector("#shippingPolicyId option:checked");
    const shippingFee = selectedOption && selectedOption.dataset.fee ? parseInt(selectedOption.dataset.fee) : 0;
    const MIN_PAYMENT_AMOUNT = 1000;
    const rawTotal = window.baseAmount + shippingFee;
    const maxUsablePoint = Math.min(window.maxPoint, rawTotal - MIN_PAYMENT_AMOUNT);

    document.getElementById("pointUsed").value = maxUsablePoint > 0 ? maxUsablePoint : 0;
    updateTotal();
}

document.addEventListener("DOMContentLoaded", () => {
    document.getElementById("pointUsed").addEventListener("input", updateTotal);
    document.getElementById("shippingPolicyId").addEventListener("change", updateTotal);
});