let selectedPaymentMethod = null;

function selectPaymentMethod(method) {
    if (selectedPaymentMethod != null) {
        document.getElementById(selectedPaymentMethod).style.backgroundColor = "#ffffff";
    }
    selectedPaymentMethod = method;
    document.getElementById(selectedPaymentMethod).style.backgroundColor = "rgb(229 239 255)";
}

const clientKey = "test_ck_kYG57Eba3G9ZZWLJKO1zrpWDOxmA";
const customerKey = generateRandomString();
const tossPayments = TossPayments(clientKey);
const payment = tossPayments.payment({ customerKey });

async function requestPayment() {
    const { amount, orderId, orderName, customerName, customerEmail } = checkoutData;

    switch (selectedPaymentMethod) {
        case "CARD":
        case "TRANSFER":
        case "VIRTUAL_ACCOUNT":
        case "MOBILE_PHONE":
        case "CULTURE_GIFT_CERTIFICATE":
        case "FOREIGN_EASY_PAY":
            const paymentOptions = {
                method: selectedPaymentMethod,
                amount,
                orderId,
                orderName,
                successUrl: window.location.origin + "/payment/process",
                failUrl: window.location.origin + "/payment/fail",
                customerEmail,
                customerName,
            };

            if (selectedPaymentMethod === "CARD") {
                paymentOptions.card = {
                    useEscrow: false,
                    flowMode: "DEFAULT",
                    useCardPoint: false,
                    useAppCardOnly: false,
                };
            } else if (selectedPaymentMethod === "TRANSFER") {
                paymentOptions.transfer = {
                    cashReceipt: { type: "소득공제" },
                    useEscrow: false,
                };
            } else if (selectedPaymentMethod === "VIRTUAL_ACCOUNT") {
                paymentOptions.virtualAccount = {
                    cashReceipt: { type: "소득공제" },
                    useEscrow: false,
                    validHours: 24,
                };
            } else if (selectedPaymentMethod === "FOREIGN_EASY_PAY") {
                paymentOptions.amount = { value: 100, currency: "USD" };
                paymentOptions.foreignEasyPay = {
                    provider: "PAYPAL",
                    country: "KR",
                };
            }

            await payment.requestPayment(paymentOptions);
            break;
    }
}

function generateRandomString() {
    return window.btoa(Math.random()).slice(0, 20);
}