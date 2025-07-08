document.addEventListener('DOMContentLoaded', function () {
    const modal = document.getElementById('cancelReasonModal');
    modal.addEventListener('show.bs.modal', function (event) {
    const button = event.relatedTarget;
    const type = button.getAttribute('data-type');
    const orderId = button.getAttribute('data-order-id');

    const form = modal.querySelector('#cancelForm');
    form.setAttribute('action', `/order/${orderId}/cancel`);
    form.querySelector('#cancelType').value = type;

    // 제목 및 버튼 텍스트 설정
    const title = type === 'refund' ? '환불 요청 사유 입력' : '주문 취소 사유 입력';
    const label = type === 'refund' ? '환불 사유' : '취소 사유';
    const buttonText = type === 'refund' ? '환불 요청 제출' : '주문 취소 제출';

    modal.querySelector('#cancelReasonLabel').textContent = title;
    modal.querySelector("label[for='cancelReason']").textContent = label;
    modal.querySelector('#submitButton').textContent = buttonText;
});
});