// src/main/resources/static/js/list.js

document.addEventListener('DOMContentLoaded', function () {
    // 키워드 태그 클릭 이벤트 핸들러
    document.querySelectorAll('.keyword-tag').forEach(tag => {
        tag.addEventListener('click', function () {
            const keyword = this.innerText;
            // 키워드를 검색어로 설정하고 폼 제출
            const searchForm = document.querySelector('.search-form');
            const searchInput = searchForm.querySelector('.search-input');
            searchInput.value = keyword;
            searchForm.submit();
        });
    });

    // 검색 폼 제출 이벤트 핸들러 (현재는 기본 동작을 유지)
    const searchForm = document.querySelector('.search-form');
    searchForm.addEventListener('submit', function (e) {
        // 필요에 따라 추가적인 로직을 여기에 작성할 수 있습니다.
    });
});
