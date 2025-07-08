// document.addEventListener("DOMContentLoaded", () => {
//     const forms = document.querySelectorAll(".needs-validation");
//     Array.from(forms).forEach(form => {
//         form.addEventListener("submit", event => {
//             if (!form.checkValidity()) {
//                 event.preventDefault();
//                 event.stopPropagation();
//             }
//             form.classList.add("was-validated");
//         });
//     });
// });

document.addEventListener("DOMContentLoaded", () => {
    const forms = document.querySelectorAll(".needs-validation");
    Array.from(forms).forEach(form => {
        form.addEventListener("submit", event => {
            let valid = true;

            const regularInput = document.getElementById("regularPrice");
            const saleInput = document.getElementById("salePrice");

            // 직접등록일떄만 검증 돌아가게끔 하기
            if (regularInput && saleInput) {
                const regular = parseInt(regularInput.value);
                const sale = parseInt(saleInput.value);

                // 판매가가 정가보다 크면 막기
                if (!isNaN(regular) && !isNaN(sale) && sale > regular) {
                    valid = false;
                    saleInput.classList.add("is-invalid");
                } else {
                    saleInput.classList.remove("is-invalid");
                }
            }

            // 기본 HTML 유효성 검증
            if (!form.checkValidity() || !valid) {
                event.preventDefault();
                event.stopPropagation();
            }

            form.classList.add("was-validated");
        });
    });
});



