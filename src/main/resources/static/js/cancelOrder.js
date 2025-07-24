document.addEventListener('DOMContentLoaded', function () {
    const modal = document.getElementById('cancelReasonModal');
    modal.addEventListener('show.bs.modal', function (event) {
        const button = event.relatedTarget;
        const type = button.getAttribute('data-type');
        const orderId = button.getAttribute('data-order-id');

        const form = modal.querySelector('#cancelForm');
        form.setAttribute('action', `/order/${orderId}/cancel`);
        form.querySelector('#cancelType').value = type;

        const title = type === 'refund' ? '환불 요청 사유 입력' : '주문 취소 사유 입력';
        const label = type === 'refund' ? '환불 사유' : '취소 사유';
        const buttonText = type === 'refund' ? '환불 요청 제출' : '주문 취소 제출';

        modal.querySelector('#cancelReasonLabel').textContent = title;
        modal.querySelector("label[for='cancelReason']").textContent = label;
        modal.querySelector('#submitButton').textContent = buttonText;
    });

    const form = document.getElementById('cancelForm');
    form.addEventListener('submit', function () {
        const submitBtn = form.querySelector('#submitButton');
        submitBtn.disabled = true;
        submitBtn.textContent = '처리 중...';
    });
});