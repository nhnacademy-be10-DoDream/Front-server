document.addEventListener("DOMContentLoaded", () => {
    initFormValidation();
    initEditor();
});

let editor = null;


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
    const dateRegex = /^\d{4}-\d{2}-\d{2}$/;

    Array.from(forms).forEach(form => {
        form.addEventListener("submit", event => {
            let valid = true;

            const regularInput = document.getElementById("regularPrice");
            const saleInput = document.getElementById("salePrice");
            const publishedAtInput = document.getElementById("publishedAt");
            const publishedAtFeedback = document.getElementById("publishedAtFeedback");


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

            // 출판일 검증
            if (publishedAtInput && publishedAtFeedback) {
                const dateValue = publishedAtInput.value.trim();

                if (!dateValue) {
                    valid = false;
                    publishedAtInput.classList.add("is-invalid");
                    publishedAtFeedback.textContent = "출판일을 선택해주세요.";
                } else if (!dateRegex.test(dateValue) || Number.isNaN(new Date(dateValue).getTime())) {
                    valid = false;
                    publishedAtInput.classList.add("is-invalid");
                    publishedAtFeedback.textContent = "날짜 형식이 맞지 않습니다.";
                } else {
                    publishedAtInput.classList.remove("is-invalid");
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
