// document.addEventListener("DOMContentLoaded", function() {
//
// });
document.addEventListener("DOMContentLoaded", function () {
    console.log("base.js: DOMContentLoaded 이벤트");

    // 기존 base.js 코드가 있다면 그대로 유지

    // comments.js 코드 시작
    console.log("comments.js 코드 실행");

    const commentForm = document.getElementById("commentForm");
    if (commentForm) {
        console.log("댓글 작성 폼이 발견되었습니다.");

        commentForm.addEventListener("submit", function (e) {
            e.preventDefault();
            console.log("댓글 작성 버튼 클릭");

            const content = document.querySelector('[name="content"]').value;

            fetch('/comments', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    content: content,
                    communityId: document.querySelector('[name="communityId"]').value,
                    memberId: document.getElementById('loggedInMemberId').value // 동적으로 가져오기
                })
            })
                .then(response => {
                    console.log("서버 응답:", response);
                    if (response.ok) {
                        console.log('댓글 등록 성공');
                        loadComments();
                    } else {
                        console.error('댓글 등록 실패');
                    }
                }).catch(error => {
                console.error('요청 중 에러 발생:', error);
            });
        });
    } else {
        console.error("댓글 작성 폼을 찾을 수 없습니다.");
    }

    function loadComments() {
        const communityId = document.querySelector('[name="communityId"]').value;

        const page = 1; // 페이지 번호
        const size = 10; // 페이지 크기

        fetch(`/comments/${communityId}?page=${page}&size=${size}`)
            .then(response => response.json())
            .then(data => {
                const list = document.getElementById('comments-list');
                if (data.dtoList && data.dtoList.length > 0) {
                    list.innerHTML = data.dtoList.map(comment => `
                    <div class="card shadow-sm mb-3">
                        <div class="card-body">
                            <div class="d-flex justify-content-between">
                                <strong>${comment.memberName}</strong>
                                <small class="text-muted">${new Date(comment.regDate).toLocaleDateString()}</small>
                            </div>
                            <p class="mt-2 mb-0">${comment.content}</p>
                        </div>
                    </div>
                `).join('');
                } else {
                    list.innerHTML = `<p class="text-center py-4 text-muted">아직 댓글이 없습니다. 첫 번째 댓글을 남겨보세요!</p>`;
                }
            });
    }

    window.showUpdateForm = function(commentId, currentContent) {
        const commentContent = prompt("댓글을 수정하세요:", currentContent);
        if (commentContent !== null) {
            fetch(`/comments/${commentId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(commentContent),
            })
                .then(response => {
                    if (response.ok) {
                        alert("댓글이 수정되었습니다.");
                        location.reload(); // 페이지 새로고침
                    } else {
                        alert("댓글 수정에 실패했습니다.");
                    }
                })
                .catch(error => console.error("Error:", error));
        }
    }

    window.deleteComment = function(commentId) {
        if (confirm("정말로 댓글을 삭제하시겠습니까?")) {
            fetch(`/comments/${commentId}`, {
                method: 'DELETE',
            })
                .then(response => {
                    if (response.ok) {
                        alert("댓글이 삭제되었습니다.");
                        location.reload(); // 페이지 새로고침
                    } else {
                        alert("댓글 삭제에 실패했습니다.");
                        console.error("삭제 실패:", response);
                    }
                })
                .catch(error => console.error("Error:", error));
        }
    };


    // 추가된 handleUpdate 함수
    window.handleUpdate = function(button) {
        const commentsId = button.getAttribute('data-comments-id'); // 댓글 ID
        const currentContent = button.getAttribute('data-comment-content'); // 현재 댓글 내용

        if (!commentsId) {
            console.error("댓글 ID를 가져오지 못했습니다.");
            return;
        }

        const updatedContent = prompt("댓글을 수정하세요:", currentContent); // 수정할 내용 입력
        if (updatedContent !== null) {
            fetch(`/comments/${commentsId}`, { // URL에 댓글 ID 포함
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ content: updatedContent }), // JSON 형식으로 수정된 내용 전달
            })
                .then(response => {
                    if (response.ok) {
                        alert("댓글이 수정되었습니다.");
                        location.reload(); // 페이지 새로고침
                    } else {
                        console.error("댓글 수정 실패:", response.status);
                        alert("댓글 수정에 실패했습니다.");
                    }
                })
                .catch(error => console.error("요청 에러:", error));
        }
    };


    // comments.js 코드 끝
});
