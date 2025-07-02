function fillAddress(select) {
    const option = select.options[select.selectedIndex];
    document.getElementById('zipcode').value = option.dataset.zipcode || '';
    document.getElementById('jibunAddress').value = option.dataset.jibun || '';
    document.getElementById('roadAddress').value = option.dataset.road || '';
    document.getElementById('detailAddress').value = option.dataset.detail || '';
    document.getElementById('extraAddress').value = option.dataset.extra || '';
}