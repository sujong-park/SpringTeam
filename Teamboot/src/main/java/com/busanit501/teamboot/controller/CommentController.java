package com.busanit501.teamboot.controller;

import com.busanit501.teamboot.domain.Member;
import com.busanit501.teamboot.repository.MemberRepository;
import com.busanit501.teamboot.service.CommentsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentsService commentsService;
    private final MemberRepository memberRepository;

    // 댓글 작성
    @PostMapping("/create")
    public String createComment(@RequestParam Long communityId,
                                @RequestParam String content,
                                @RequestParam(required = false) Integer page,
                                Principal principal) {
        if (principal == null) {
            // 로그인되지 않은 경우
            return "redirect:/login";
        }

        // 현재 로그인된 사용자의 이메일로 Member 조회
        Member member = memberRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 댓글 생성
        commentsService.createComment(communityId, member, content);

        return page != null
                ? "redirect:/communities/" + communityId + "?page=" + page
                : "redirect:/communities/" + communityId;
    }

    // 댓글 삭제
    @PostMapping("/delete/{id}")
    public String deleteComment(@PathVariable Long id,
                                Principal principal,
                                @RequestParam Long communityId,
                                RedirectAttributes redirectAttributes) {
        if (principal == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "로그인이 필요합니다.");
            return "redirect:/login";
        }

        Member member = memberRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        commentsService.deleteComment(id, member);

        return "redirect:/communities/" + communityId;
    }
}
