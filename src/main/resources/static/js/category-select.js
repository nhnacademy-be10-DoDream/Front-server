document.addEventListener('DOMContentLoaded', () => {

    const categoryJsonData = document.getElementById('categoryJsonData').value;
    const categoryList = JSON.parse(categoryJsonData);

    const wrapper = document.getElementById('category-select-wrapper');
    const selectedCategoryIdInput = document.getElementById('selectedCategoryId');

    // 현재 선택된 카테고리 ID 단계별로 저장 (depth 별로 index)
    let selectedIds = [];

    // 카테고리 데이터를 depth 별로 그룹핑 (parentId 기준)
    const categoriesByParent = {};
    categoryList.forEach(cat => {
        const pid = cat.parentId === null ? 0 : cat.parentId;
        if (!categoriesByParent[pid]) categoriesByParent[pid] = [];
        categoriesByParent[pid].push(cat);
    });

    // select 요소 생성 함수
    function createSelect(depth) {
        const select = document.createElement('select');
        select.classList.add('form-select', 'form-select-sm');
        select.style.minWidth = '150px';
        select.dataset.depth = depth;

        const defaultOption = document.createElement('option');
        defaultOption.value = '';
        defaultOption.textContent = '-- 선택 --';
        select.appendChild(defaultOption);

        // 이전 단계 선택한 카테고리 ID로 자식 카테고리 리스트 뿌리기
        const parentId = depth === 0 ? 0 : selectedIds[depth - 1] || 0;
        const options = categoriesByParent[parentId] || [];

        options.forEach(cat => {
            const option = document.createElement('option');
            option.value = cat.categoryId;
            option.textContent = cat.categoryName;
            select.appendChild(option);
        });

        // 변경 이벤트
        select.addEventListener('change', () => {
            const selectedValue = select.value ? Number(select.value) : null;
            selectedIds = selectedIds.slice(0, depth);
            if (selectedValue) selectedIds[depth] = selectedValue;

            // 하위 select 제거
            const toRemove = Array.from(wrapper.querySelectorAll('select'))
                .filter(s => Number(s.dataset.depth) > depth);
            toRemove.forEach(s => wrapper.removeChild(s));

            // 하위 카테고리가 있으면 select 추가
            if (selectedValue && categoriesByParent[selectedValue]) {
                createSelect(depth + 1);
            }

            // 최종 선택값을 숨겨진 input에 저장
            if (selectedIds.length > 0) {
                selectedCategoryIdInput.value = selectedIds[selectedIds.length - 1];
            } else {
                selectedCategoryIdInput.value = '';
            }
        });

        wrapper.appendChild(select);
    }

    // 초기 최상위 카테고리 select 생성
    createSelect(0);
});


document.addEventListener('DOMContentLoaded', () => {
    const categoryJsonData = document.getElementById('categoryJsonData');
    if (!categoryJsonData) return;

    const categories = JSON.parse(categoryJsonData.value);

    // 각 수정 폼별 category-select-wrapper에 초기 1단계 select 생성
    document.querySelectorAll('.category-select-wrapper').forEach(wrapper => {
        // 초기 select 1개 생성
        createSelect(wrapper, 1, categories.filter(c => c.depth === 1));
    });

    // select 변경 이벤트는 위 createSelect 함수 안에서 각각 할당됨

    // 최종 선택값 hidden input 업데이트 함수
    function updateSelectedCategoryId(wrapper) {
        const selects = wrapper.querySelectorAll('select');
        let lastSelected = null;

        selects.forEach(sel => {
            if (sel.value) lastSelected = sel.value;
        });

        const form = wrapper.closest('form');
        if (form) {
            const hiddenInput = form.querySelector('input[name="newCategoryId"]');
            if (hiddenInput) hiddenInput.value = lastSelected || '';
        }
    }

    // select 생성 함수: depth 단계, 옵션들 전달
    function createSelect(wrapper, depth, options) {
        const select = document.createElement('select');
        select.className = 'form-select form-select-sm w-auto';
        select.dataset.depth = depth;
        select.style.minWidth = '120px';

        // 기본 옵션
        const defaultOption = document.createElement('option');
        defaultOption.value = '';
        defaultOption.textContent = `--  선택 --`;
        select.appendChild(defaultOption);

        options.forEach(opt => {
            const option = document.createElement('option');
            option.value = opt.categoryId;
            option.textContent = opt.categoryName;
            option.dataset.parentId = opt.parentId;
            option.dataset.depth = opt.depth;
            select.appendChild(option);
        });

        // change 이벤트: 선택하면 다음 select 생성 & 이후 select 제거
        select.addEventListener('change', () => {
            // 현재 선택값
            const selectedValue = select.value;

            // 현재 depth 이후 모든 select 삭제
            const selects = [...wrapper.querySelectorAll('select')];
            selects.forEach(s => {
                if (parseInt(s.dataset.depth) > depth) {
                    s.remove();
                }
            });

            if (selectedValue) {
                // 선택한 카테고리의 자식 카테고리 목록 찾기
                const childOptions = categories.filter(c => String(c.parentId) === selectedValue);

                // 자식 카테고리가 있으면 다음 단계 select 생성
                if (childOptions.length > 0 && depth < 5) {
                    createSelect(wrapper, depth + 1, childOptions);
                }
            }

            updateSelectedCategoryId(wrapper);
        });

        wrapper.appendChild(select);
    }
});


