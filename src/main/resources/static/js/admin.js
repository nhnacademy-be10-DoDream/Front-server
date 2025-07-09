document.addEventListener("DOMContentLoaded", () => {
    initFormValidation();
});



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
