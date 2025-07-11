document.addEventListener("DOMContentLoaded", () => {
    initFormValidation();
    initEditor();
});



function initEditor() {
    const editorEl = document.getElementById("editor");
    if (editorEl) {
        editor = new toastui.Editor({
            el: editorEl,
            height: '400px',
            initialEditType: 'markdown',
            previewStyle: 'vertical'
        });
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

            // Toast UI Editor 값 hidden textarea에 세팅
            if (editor) {
                let hiddenTextarea = document.querySelector('textarea[name="description"]');
                if (!hiddenTextarea) {
                    hiddenTextarea = document.createElement('textarea');
                    hiddenTextarea.name = 'description';
                    hiddenTextarea.style.display = 'none';
                    form.appendChild(hiddenTextarea);
                }
                hiddenTextarea.value = editor.getHTML();
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
