(function () {
    'use strict';

    // =====================================
    // 상수 정의
    // =====================================
    const CONFIG = {
        FILE_PATH: {
            VALIDATION: './validation.js'
        },
        SELECTORS: {
            EMAIL_INPUT: '#email',
            EMAIL_FEEDBACK: '#emailFeedback',
            POSTCODE_BUTTON: '#postcodeButton',
            POSTCODE_LAYER: '#postcodeLayer',
            POSTCODE_LAYER_INNER: '#postcodeLayerInner',
            POSTCODE_CLOSE_BTN: '#btnCloseLayer',
            USER_UPDATE_MODAL_ID: 'userUpdateModal',
            USER_UPDATE_MODAL: '#userUpdateModal',
            USER_UPDATE_PASSWORD_MODAL_ID: 'userPasswordUpdateModal',
            USER_UPDATE_PASSWORD_MODAL: '#userPasswordUpdateModal',
            ADDRESS_MODAL: '#addressModal',
            ADDRESS_FORM: '#addressForm',
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
    let lastFocusedElement = null;
    let emailChecked = false;

    // =====================================
    // 모달 접근성 관리
    // =====================================
    function initModalAccessibility() {
        const modals = document.querySelectorAll(CONFIG.ACCESSIBILITY.MODAL_SELECTORS);

        modals.forEach(modal => {
            modal.addEventListener('show.bs.modal', (e) => {
                lastFocusedElement = document.activeElement;

                document.querySelectorAll(CONFIG.ACCESSIBILITY.INERT_TARGETS).forEach(el => {
                    if (!el.contains(modal)) {
                        el.setAttribute('inert', '');
                    }
                });
            });

            modal.addEventListener('shown.bs.modal', () => {
                modal.removeAttribute('aria-hidden');
                modal.setAttribute('aria-modal', 'true');

                const firstFocusable = modal.querySelector(
                    'input:not([disabled]), button:not([disabled]), textarea:not([disabled]), select:not([disabled]), [tabindex]:not([tabindex="-1"])'
                );
                if (firstFocusable) {
                    firstFocusable.focus();
                }
            });

            modal.addEventListener('hide.bs.modal', () => {
                if (document.activeElement && modal.contains(document.activeElement)) {
                    document.activeElement.blur();
                }
            });

            modal.addEventListener('hidden.bs.modal', () => {
                document.querySelectorAll('[inert]').forEach(el => {
                    el.removeAttribute('inert');
                });

                modal.removeAttribute('aria-modal');

                if (lastFocusedElement && typeof lastFocusedElement.focus === 'function') {
                    lastFocusedElement.focus();
                }
                lastFocusedElement = null;
            });
        });
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
    // 페이지별 초기화 함수들
    // =====================================
    function initProfilePage() {
        const validationModulePromise = import(CONFIG.FILE_PATH.VALIDATION);

        const handleModalShow = async (modalId) => {
            try {
                const module = await validationModulePromise;

                if (modalId === CONFIG.SELECTORS.USER_UPDATE_MODAL_ID) {
                    module.initEmailValidation(CONFIG.SELECTORS.USER_UPDATE_MODAL, (ok) => {
                        emailChecked = ok;
                    });
                } else if (modalId === CONFIG.SELECTORS.USER_UPDATE_PASSWORD_MODAL_ID) {
                    module.initPasswordValidation(CONFIG.SELECTORS.USER_UPDATE_PASSWORD_MODAL);
                }
            } catch (err) {
                console.error('Validation Loading Failed', err);
            }
        };

        ['#userUpdateModal', '#userPasswordUpdateModal'].forEach(selector => {
            const modal = document.querySelector(selector);
            if (modal) {
                modal.addEventListener('shown.bs.modal', () => {
                    handleModalShow(modal.id).catch(err => {
                        console.error('Modal show handling failed:', err);
                    });
                }, { once: true });
            }
        });

        initUserEditModal();
        initModalAccessibility();
    }



    function initAddressPage() {
        initAddressModal();
        initAddressDetails();
        initAddressAPI();
        initModalAccessibility();
    }

    function initOrdersPage() {
    }

    // =====================================
    // activeMenu 기반 스마트 초기화 (성능 최적화)
    // =====================================
    function smartInitByActiveMenu() {
        const activeMenu = document.body.dataset.activeMenu ||
            document.querySelector('[data-active-menu]')?.dataset.activeMenu;

        switch (activeMenu) {
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
                initModalAccessibility();
                break;
            case 'reviews':
                initModalAccessibility();
                break;
            default:
                console.log('activeMenu가 설정되지 않음');
                initModalAccessibility();
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
