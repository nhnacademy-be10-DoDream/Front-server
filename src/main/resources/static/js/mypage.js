(function() {
    'use strict';

    // =====================================
    // 상수 정의
    // =====================================
    const CONFIG = {
        API_ENDPOINTS: {
            CHECK_EMAIL: '/api/user/check-email',
            CANCEL_ORDER: '/api/order/cancel'
        },
        SELECTORS: {
            EMAIL_INPUT: '#email',
            EMAIL_FEEDBACK: '#emailFeedback',
            POSTCODE_BUTTON: '#postcodeButton',
            POSTCODE_LAYER: '#postcodeLayer',
            POSTCODE_LAYER_INNER: '#postcodeLayerInner',
            POSTCODE_CLOSE_BTN: '#btnCloseLayer',
            USER_UPDATE_MODAL: '#userUpdateModal',
            ADDRESS_MODAL: '#addressModal',
            ADDRESS_FORM: '#addressForm',
            USER_UPDATE_PASSWORD_FORM: '#userUpdatePasswordForm',
            NEW_PASSWORD: '#newPassword',
            CONFIRM_PASSWORD: '#confirmPassword'
        },
        // 접근성 관리 설정 추가
        ACCESSIBILITY: {
            INERT_TARGETS: 'main, aside, nav, header, footer, .sidebar, .content',
            MODAL_SELECTORS: '.modal'
        }
    };

    // =====================================
    // 지역 상태 관리 (전역 오염 방지)
    // =====================================
    let emailChecked = false;
    let lastFocusedElement = null; // 포커스 복원용

    // =====================================
    // 모달 접근성 관리 (aria-hidden 경고 해결)
    // =====================================
    function initModalAccessibility() {
        const modals = document.querySelectorAll(CONFIG.ACCESSIBILITY.MODAL_SELECTORS);

        modals.forEach(modal => {
            // 모달이 열리기 시작할 때
            modal.addEventListener('show.bs.modal', (e) => {
                // 현재 포커스된 요소 저장
                lastFocusedElement = document.activeElement;

                // 배경 요소들을 inert로 설정
                document.querySelectorAll(CONFIG.ACCESSIBILITY.INERT_TARGETS).forEach(el => {
                    if (!el.contains(modal)) {
                        el.setAttribute('inert', '');
                    }
                });
            });

            // 모달이 완전히 열린 후
            modal.addEventListener('shown.bs.modal', () => {
                // aria-hidden 제거 및 aria-modal 추가
                modal.removeAttribute('aria-hidden');
                modal.setAttribute('aria-modal', 'true');

                // 모달 내 첫 번째 포커스 가능한 요소에 포커스
                const firstFocusable = modal.querySelector(
                    'input:not([disabled]), button:not([disabled]), textarea:not([disabled]), select:not([disabled]), [tabindex]:not([tabindex="-1"])'
                );
                if (firstFocusable) {
                    firstFocusable.focus();
                }
            });

            // 모달이 닫히기 시작할 때
            modal.addEventListener('hide.bs.modal', () => {
                // 현재 포커스 제거
                if (document.activeElement && modal.contains(document.activeElement)) {
                    document.activeElement.blur();
                }
            });

            // 모달이 완전히 닫힌 후
            modal.addEventListener('hidden.bs.modal', () => {
                // 모든 inert 속성 제거
                document.querySelectorAll('[inert]').forEach(el => {
                    el.removeAttribute('inert');
                });

                // aria-modal 제거
                modal.removeAttribute('aria-modal');

                // 원래 포커스된 요소로 복원
                if (lastFocusedElement && typeof lastFocusedElement.focus === 'function') {
                    lastFocusedElement.focus();
                }
                lastFocusedElement = null;
            });
        });
    }

    // =====================================
    // 이메일 중복체크 기능 - async/await 적용
    // =====================================
    async function checkEmailDuplicate(email) {
        try {
            const response = await fetch(`${CONFIG.API_ENDPOINTS.CHECK_EMAIL}?email=${encodeURIComponent(email)}`);
            if (!response.ok) throw new Error('네트워크 응답 오류');
            const data = await response.json();
            return data.duplicate;
        } catch (error) {
            throw new Error('이메일 중복확인 중 오류가 발생했습니다.');
        }
    }

    function initEmailValidation() {
        const checkBtn = document.querySelector(CONFIG.SELECTORS.EMAIL_FEEDBACK)?.previousElementSibling;
        const emailInput = document.querySelector(CONFIG.SELECTORS.EMAIL_INPUT);
        const feedback = document.querySelector(CONFIG.SELECTORS.EMAIL_FEEDBACK);

        if (!checkBtn || !emailInput || !feedback) return;

        checkBtn.addEventListener('click', async () => {
            const email = emailInput.value.trim();

            if (!email || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
                showFeedback('올바른 이메일 형식을 입력해주세요.', false);
                return;
            }

            try {
                const isDuplicate = await checkEmailDuplicate(email);
                if (isDuplicate) {
                    showFeedback('이미 사용 중인 이메일입니다.', false);
                    emailChecked = false;
                } else {
                    showFeedback('사용 가능한 이메일입니다.', true);
                    emailChecked = true;
                }
            } catch (error) {
                showFeedback(error.message, false);
                emailChecked = false;
            }
        });

        emailInput.addEventListener('input', () => {
            emailChecked = false;
            resetValidation();
        });

        function showFeedback(message, isValid) {
            feedback.textContent = message;
            feedback.className = `d-block ${isValid ? 'text-success' : 'text-danger'}`;
            // 접근성 개선
            feedback.setAttribute('aria-live', 'polite');
            feedback.setAttribute('role', 'status');
            emailInput.className = isValid ? 'form-control is-valid' : 'form-control is-invalid';
        }

        function resetValidation() {
            feedback.textContent = '';
            feedback.className = '';
            emailInput.className = 'form-control';
        }
    }

    // =====================================
    // 사용자 정보 수정 모달
    // =====================================
    function initUserEditModal() {
        const modal = document.querySelector(CONFIG.SELECTORS.USER_UPDATE_MODAL);
        if (!modal) return;

        modal.addEventListener('show.bs.modal', (e) => {
            const btn = e.relatedTarget;
            if (!btn?.classList.contains('user-edit-modal')) return;

            ['email', 'name', 'phone', 'birthDate'].forEach(field => {
                const input = modal.querySelector(`#${field}`);
                if (input) {
                    input.value = btn.getAttribute(`data-${field.toLowerCase()}`) || '';
                }
            });
            emailChecked = false;
        });

        const form = modal.querySelector('form');
        if (form) {
            form.addEventListener('submit', (e) => {
                const emailInput = modal.querySelector(CONFIG.SELECTORS.EMAIL_INPUT);
                if (emailInput && !emailChecked) {
                    e.preventDefault();
                    alert('이메일 중복확인을 먼저 진행해주세요.');
                    emailInput.focus();
                }
            });
        }
    }

    // =====================================
    // 비밀번호 검증
    // =====================================
    function initPasswordValidation() {
        const form = document.querySelector(CONFIG.SELECTORS.USER_UPDATE_PASSWORD_FORM);
        const newPwd = document.querySelector(CONFIG.SELECTORS.NEW_PASSWORD);
        const confirmPwd = document.querySelector(CONFIG.SELECTORS.CONFIRM_PASSWORD);

        if (!form || !newPwd || !confirmPwd) return;

        form.addEventListener('submit', (e) => {
            if (newPwd.value !== confirmPwd.value) {
                e.preventDefault();
                confirmPwd.classList.add('is-invalid');
                const feedback = confirmPwd.nextElementSibling;
                if (feedback) feedback.textContent = '비밀번호가 일치하지 않습니다.';
            }
        });
    }

    // =====================================
    // 주소 관리 모달
    // =====================================
    function initAddressModal() {
        const modal = document.querySelector(CONFIG.SELECTORS.ADDRESS_MODAL);
        if (!modal) return;

        modal.addEventListener('show.bs.modal', (e) => {
            const btn = e.relatedTarget;
            const isEdit = btn.classList.contains('address-edit-modal');

            const methodInput = document.getElementById('_method');
            const form = document.querySelector(CONFIG.SELECTORS.ADDRESS_FORM);

            if (methodInput) methodInput.value = isEdit ? 'put' : 'post';
            if (form) form.action = isEdit ? `/mypage/addresses/${btn.dataset.addressId}` : '/mypage/addresses';

            ['alias', 'roadAddress', 'zipcode', 'jibunAddress', 'detailAddress', 'extraAddress'].forEach(field => {
                const element = document.getElementById(field);
                if (element) element.value = btn.dataset[field] || '';
            });
        });
    }

    // =====================================
    // 주소 상세 보기
    // =====================================
    function initAddressDetails() {
        const radios = document.querySelectorAll("input[name='address-radio']");
        const details = document.querySelectorAll('.address-detail');

        if (!radios.length || !details.length) return;

        radios.forEach(radio => {
            radio.addEventListener('change', () => showAddressDetail(radio.value));
        });

        const checked = document.querySelector("input[name='address-radio']:checked");
        if (checked) showAddressDetail(checked.value);

        function showAddressDetail(id) {
            details.forEach(detail => {
                if (detail.id === `address-detail-${id}`) {
                    detail.style.display = 'flex';
                    detail.style.flex = '1';
                    detail.style.flexDirection = 'column';
                } else {
                    detail.style.display = 'none';
                }
            });

            document.querySelectorAll('.address-item').forEach(item => item.classList.remove('active'));
            document.querySelector(`label[for='address-radio-${id}']`)?.classList.add('active');
        }
    }

    // =====================================
    // 다음 주소 API
    // =====================================
    function initAddressAPI() {
        const postcodeBtn = document.querySelector(CONFIG.SELECTORS.POSTCODE_BUTTON);
        if (!postcodeBtn) return;

        postcodeBtn.addEventListener('click', (e) => {
            e.preventDefault();

            if (typeof daum === 'undefined') {
                alert('주소 검색 서비스를 사용할 수 없습니다.');
                return;
            }

            const layer = document.querySelector(CONFIG.SELECTORS.POSTCODE_LAYER);
            const innerLayer = document.querySelector(CONFIG.SELECTORS.POSTCODE_LAYER_INNER);

            new daum.Postcode({
                oncomplete: (data) => {
                    document.getElementById('zipcode').value = data.zonecode;
                    document.getElementById('roadAddress').value = data.roadAddress;
                    document.getElementById('jibunAddress').value = data.jibunAddress;

                    let extraAddr = '';
                    if (data.bname && /[동|로|가]$/g.test(data.bname)) extraAddr += data.bname;
                    if (data.buildingName && data.apartment === 'Y') {
                        extraAddr += (extraAddr ? ', ' : '') + data.buildingName;
                    }
                    document.getElementById('extraAddress').value = extraAddr ? ` (${extraAddr})` : '';

                    layer.style.display = 'none';
                },
                width: '100%',
                height: '100%'
            }).embed(innerLayer);

            layer.style.display = 'block';
        });

        document.querySelector(CONFIG.SELECTORS.POSTCODE_CLOSE_BTN)?.addEventListener('click', () => {
            document.querySelector(CONFIG.SELECTORS.POSTCODE_LAYER).style.display = 'none';
        });
    }

    // =====================================
    // 주문 관련 기능 - 이벤트 위임 적용 (성능 최적화)
    // =====================================
    function initOrderActions() {
        document.addEventListener('click', (e) => {
            const target = e.target;
            if (!(target instanceof HTMLElement)) return;
            const action = target.dataset.action;

            if (!action) return;

            switch(action) {
                case 'write-review':
                {
                    const orderId = target.dataset.orderId;
                    if (orderId) location.href = `/review/write?orderId=${orderId}`;
                }
                    break;
                case 'cancel-order':
                {
                    const orderId = target.dataset.orderId;
                    if (!orderId) return;
                    if (!confirm('정말로 주문을 취소하시겠습니까?')) return;

                    const originalText = target.textContent;
                    target.textContent = '취소중...';
                    target.disabled = true;

                    fetch(`${CONFIG.API_ENDPOINTS.CANCEL_ORDER}/${orderId}`, {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' }
                    })
                        .then(res => res.json())
                        .then(data => {
                            if (data.success) {
                                alert('주문이 취소되었습니다.');
                                location.reload();
                            } else {
                                alert(data.message || '주문 취소에 실패했습니다.');
                            }
                        })
                        .catch(() => alert('오류가 발생했습니다.'))
                        .finally(() => {
                            target.textContent = originalText;
                            target.disabled = false;
                        });
                }
                    break;
                case 'request-return':
                {
                    const orderId = target.dataset.orderId;
                    if (orderId && confirm('반품을 신청하시겠습니까?')) {
                        location.href = `/order/return?orderId=${orderId}`;
                    }
                }
                    break;
            }
        });
    }



    // =====================================
    // 페이지별 초기화 함수들
    // =====================================
    function initProfilePage() {
        initEmailValidation();
        initUserEditModal();
        initPasswordValidation();
        initModalAccessibility();
    }

    function initAddressPage() {
        initAddressModal();
        initAddressDetails();
        initAddressAPI();
        initModalAccessibility();
    }

    function initOrdersPage() {
        initOrderActions();
    }

    // =====================================
    // activeMenu 기반 스마트 초기화 (성능 최적화)
    // =====================================
    function smartInitByActiveMenu() {
        const activeMenu = document.body.dataset.activeMenu ||
            document.querySelector('[data-active-menu]')?.dataset.activeMenu;

        switch(activeMenu) {
            case 'profile':
                initProfilePage();
                break;
            case 'addresses':
                initAddressPage();
                break;
            case 'orders':
                initOrdersPage();
                break;
            case 'points':
                initModalAccessibility(); // 모달이 있을 경우 대비
                break;
            case 'reviews':
                initModalAccessibility(); // 모달이 있을 경우 대비
                break;
            default:
                console.log('activeMenu가 설정되지 않음');
                initModalAccessibility(); // 안전장치
        }
    }

    // =====================================
    // 초기화 실행
    // =====================================
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', smartInitByActiveMenu);
    } else {
        smartInitByActiveMenu();
    }

})();
