package com.busanit501.teamboot.controller;

import com.busanit501.teamboot.domain.Member;
import com.busanit501.teamboot.repository.MemberRepository;
import com.busanit501.teamboot.service.CommentsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Map;
@Log4j2
@RestController // Restful Controller를 위해 @RestController 사용
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentsService commentsService;
    private final MemberRepository memberRepository;

    // 댓글 작성
    @PostMapping("/create")
    public ResponseEntity<String> createComment(@RequestBody Map<String, Object> requestData, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        Long communityId = Long.valueOf(requestData.get("communityId").toString());
        String content = requestData.get("content").toString();

        // 현재 로그인된 사용자의 이메일로 Member 조회
        Member member = memberRepository.findByMid(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 댓글 생성
        commentsService.createComment(communityId, member, content);

        return ResponseEntity.ok("댓글이 추가되었습니다.");
    }

    // 댓글 삭제
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteComment(@PathVariable Long id, Principal principal) {
        log.info("Comment ID: {}", id);

        if (principal == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        Member member = memberRepository.findByMid(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        try {
            commentsService.deleteComment(id, member);
            return ResponseEntity.ok("댓글이 삭제되었습니다.");
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body("삭제 권한이 없습니다.");
        } catch (Exception e) {
            log.error("댓글 삭제 중 오류 발생", e);
            return ResponseEntity.status(500).body("댓글 삭제 중 오류가 발생했습니다.");
        }
    }
}