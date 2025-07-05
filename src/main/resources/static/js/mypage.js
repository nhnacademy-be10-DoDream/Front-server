// 다음 주소 API 실행
function execDaumPostcode(event) {
    event?.preventDefault();
    event?.stopPropagation();

    const elementLayer = document.getElementById('postcodeLayer');
    const innerLayer = document.getElementById('postcodeLayerInner');

    new daum.Postcode({
        oncomplete: function (data) {
            const roadAddr = data.roadAddress;
            let extraRoadAddr = '';

            if (data.bname && /[동|로|가]$/g.test(data.bname)) {
                extraRoadAddr += data.bname;
            }
            if (data.buildingName && data.apartment === 'Y') {
                extraRoadAddr += (extraRoadAddr ? ', ' : '') + data.buildingName;
            }
            if (extraRoadAddr) {
                extraRoadAddr = ` (${extraRoadAddr})`;
            }

            document.getElementById('zipcode').value = data.zonecode;
            document.getElementById('roadAddress').value = roadAddr;
            document.getElementById('jibunAddress').value = data.jibunAddress;
            document.getElementById('extraAddress').value = roadAddr ? extraRoadAddr : '';

            elementLayer.style.display = 'none';
        },
        width: '100%',
        height: '100%',
    }).embed(innerLayer);

    elementLayer.style.display = 'block';
}

// 다음 주소 레이어 닫기
function closeDaumPostcode() {
    document.getElementById('postcodeLayer').style.display = 'none';
}

// 주소 모달 초기화
function initModalForm({ isEdit, data = {} }) {
    const methodInput = document.getElementById('_method');
    const form = document.getElementById('addressForm');
    const modalTitle = document.getElementById('addressModalTitle');

    if (!methodInput || !form || !modalTitle) return;

    methodInput.value = isEdit ? 'put' : 'post';
    form.action = isEdit
        ? `/mypage/addresses/${data.addressId}`
        : '/mypage/addresses';
    modalTitle.textContent = isEdit ? '주소 수정' : '주소 추가';

    const fields = [
        'alias',
        'roadAddress',
        'zipcode',
        'jibunAddress',
        'detailAddress',
        'extraAddress',
    ];

    fields.forEach((field) => {
        const element = document.getElementById(field);
        if (element) {
            element.value = data[field] || '';
        }
    });
}

// 주소 상세 보기 제어
function setupAddressDetails() {
    const radios = document.querySelectorAll("input[name='address-radio']");
    const details = document.querySelectorAll('.address-detail');

    if (radios.length === 0 || details.length === 0) return;

    function showDetail(id) {
        details.forEach((detail) => {
            detail.style.display = detail.id === `address-detail-${id}` ? 'block' : 'none';
        });

        document.querySelectorAll('.address-item').forEach((label) => {
            label.classList.remove('active');
        });

        const label = document.querySelector(`label[for='address-radio-${id}']`);
        if (label) label.classList.add('active');
    }

    radios.forEach((radio) => {
        radio.addEventListener('change', function () {
            showDetail(this.value);
        });
    });

    const firstChecked = document.querySelector("input[name='address-radio']:checked");
    if (firstChecked) {
        showDetail(firstChecked.value);
    }
}

// 사용자 정보 수정 모달 초기화
function setupUserEditModal() {
    const modal = document.getElementById('userUpdateModal');
    if (!modal) return;

    modal.addEventListener('show.bs.modal', function (event) {
        const button = event.relatedTarget;
        if (!button.classList.contains('open-edit-modal')) return;

        const fields = ['email', 'name', 'phone', 'birthDate'];
        fields.forEach((field) => {
            const value = button.getAttribute(`data-${field.toLowerCase()}`) || '';
            const input = modal.querySelector(`#${field}`);
            if (input) input.value = value;
        });
    });
}

// 주소 모달 이벤트 연결
function setupModalEvent() {
    const modal = document.getElementById('addressModal');
    if (!modal) return;

    modal.addEventListener('show.bs.modal', function (event) {
        const button = event.relatedTarget;
        const isEdit = button.classList.contains('open-edit-modal');

        const data = {
            addressId: button.dataset.addressId,
            alias: button.dataset.alias,
            roadAddress: button.dataset.roadAddress,
            zipcode: button.dataset.zipcode,
            jibunAddress: button.dataset.jibunAddress,
            detailAddress: button.dataset.detailAddress,
            extraAddress: button.dataset.extraAddress,
        };

        initModalForm({ isEdit, data });
        document.querySelector('#addressModal .btn-close')?.focus();
    });
}

// 리뷰 작성 페이지 이동
function writeReview(orderId) {
    window.location.href = '/review/write?orderId=' + orderId;
}

// 주문 취소 처리
function cancelOrder(orderId, buttonElement) {
    if (!confirm('정말로 주문을 취소하시겠습니까?')) return;

    const originalContent = buttonElement.innerHTML;
    buttonElement.innerHTML = '<span class="spinner-border spinner-border-sm me-1"></span>취소중...';
    buttonElement.classList.add('btn-loading');
    buttonElement.disabled = true;

    fetch('/api/order/cancel/' + orderId, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
    })
        .then((response) => response.json())
        .then((data) => {
            if (data.success) {
                alert('주문이 취소되었습니다.');
                location.reload();
            } else {
                alert(data.message || '주문 취소에 실패했습니다.');
            }
        })
        .catch((error) => {
            console.error('Error:', error);
            alert('오류가 발생했습니다. 다시 시도해주세요.');
        })
        .finally(() => {
            buttonElement.innerHTML = originalContent;
            buttonElement.classList.remove('btn-loading');
            buttonElement.disabled = false;
        });
}

// 반품 신청 이동
function requestReturn(orderId) {
    if (confirm('반품을 신청하시겠습니까?')) {
        window.location.href = '/order/return?orderId=' + orderId;
    }
}

// 페이지 로드시 주문 상태별 개수 출력
document.addEventListener('DOMContentLoaded', function () {
    const statusCounts = {
        WAITING: document.querySelectorAll('.status-WAITING').length,
        DELIVERING: document.querySelectorAll('.status-DELIVERING').length,
        COMPLETED: document.querySelectorAll('.status-COMPLETED').length,
        RETURNED: document.querySelectorAll('.status-RETURNED').length,
        CANCELED: document.querySelectorAll('.status-CANCELED').length,
    };

    console.log('주문 상태별 현황:', statusCounts);
});

// 모달 닫힐 때 포커스 해제
document.getElementById('addressModal')?.addEventListener('hide.bs.modal', function () {
    document.activeElement.blur();
    document.body.focus();
});

// 페이지 로드시 초기화 함수 실행
document.addEventListener('DOMContentLoaded', function () {
    setupModalEvent();
    setupAddressDetails();
    setupUserEditModal();
});