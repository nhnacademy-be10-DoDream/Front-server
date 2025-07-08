document.addEventListener("DOMContentLoaded", () => {
    initBookEditModal();
    initFormValidation();
});

/**
 * 도서 수정 모달 show 시 기존 데이터 세팅
 */
function initBookEditModal() {
    const modal = document.getElementById('bookEditModal');
    if (!modal) return;

    modal.addEventListener('show.bs.modal', function (event) {
        const button = event.relatedTarget;
        if (!button) return;

        // 필드 세팅
        setModalField('modal-bookId', button.getAttribute('data-book-id'));
        setModalField('modal-title', button.getAttribute('data-title'));
        setModalField('modal-author', button.getAttribute('data-author'));
        setModalField('modal-publisher', button.getAttribute('data-publisher'));
        setModalField('modal-isbn', button.getAttribute('data-isbn'));
        setModalField('modal-publishedAt', button.getAttribute('data-published-at'));
        setModalField('modal-regularPrice', button.getAttribute('data-regular-price'));
        setModalField('modal-salePrice', button.getAttribute('data-sale-price'));
        setModalField('modal-discountRate', button.getAttribute('data-discount-rate'));
        setModalField('modal-status', button.getAttribute('data-status'));
        setModalField('modal-bookCount', button.getAttribute('data-book-count'));
        setModalField('modal-description', button.getAttribute('data-description'));
    });
}

/**
 * 특정 ID의 input 필드에 값 설정
 */
function setModalField(id, value) {
    const el = document.getElementById(id);
    if (el) {
        el.value = value || '';
    }
}

/**
 * 도서 등록/수정 폼 유효성 검사 처리
 */
function initFormValidation() {
    const forms = document.querySelectorAll(".needs-validation");

    Array.from(forms).forEach(form => {
        form.addEventListener("submit", event => {
            let valid = true;

            const regularInput = document.getElementById("regularPrice");
            const saleInput = document.getElementById("salePrice");

            // 정가/판매가 검증
            if (regularInput && saleInput) {
                const regular = parseInt(regularInput.value);
                const sale = parseInt(saleInput.value);

                if (!isNaN(regular) && !isNaN(sale) && sale > regular) {
                    valid = false;
                    saleInput.classList.add("is-invalid");
                } else {
                    saleInput.classList.remove("is-invalid");
                }
            }

            // 기본 HTML 유효성 + 커스텀 유효성
            if (!form.checkValidity() || !valid) {
                event.preventDefault();
                event.stopPropagation();
            }

            form.classList.add("was-validated");
        });
    });
}
