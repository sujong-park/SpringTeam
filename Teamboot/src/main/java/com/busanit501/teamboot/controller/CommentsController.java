package com.busanit501.teamboot.controller;

import com.busanit501.bootproject.dto.CommentsDTO;
import com.busanit501.bootproject.dto.PageResponseDTO;
import com.busanit501.bootproject.security.dto.MemberSecurityDTO;
import com.busanit501.bootproject.service.CommentsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentsController {

    private final CommentsService commentsService;

    // 댓글 작성
    @PostMapping
    public ResponseEntity<Long> createComment(@RequestBody CommentsDTO commentsDTO,
                                              @AuthenticationPrincipal MemberSecurityDTO loggedInUser) {
        commentsDTO.setMemberId(loggedInUser.getMid());
        Long commentId = commentsService.createComment(commentsDTO);
        return ResponseEntity.ok(commentId);
    }

    // 댓글 목록 조회
    @GetMapping("/{communityId}")
    public ResponseEntity<PageResponseDTO<CommentsDTO>> getComments(
            @PathVariable Long communityId,
            @RequestParam(defaultValue = "1") int page,  // 페이지 번호 (기본값: 1)
            @RequestParam(defaultValue = "10") int size // 페이지 크기 (기본값: 10)
    ) {
        PageResponseDTO<CommentsDTO> commentsPage = commentsService.getCommentsByCommunity(communityId, page, size);
        return ResponseEntity.ok(commentsPage);
    }

    // 댓글 수정
    @PutMapping("/{id}")  // "/comments"는 이미 RequestMapping에 지정됨
    public ResponseEntity<Void> updateComment(
            @PathVariable Long id,
            @RequestBody CommentsDTO commentDTO) {
        log.info("수정 요청: 댓글 ID={}, 내용={}", id, commentDTO.getContent());
        commentsService.updateComment(id, commentDTO.getContent());
        return ResponseEntity.ok().build();
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        log.info("삭제 요청: 댓글 ID={}", commentId);
        commentsService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}
