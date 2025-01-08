package com.busanit501.teamboot.service;

import com.busanit501.teamboot.domain.Comments;
import com.busanit501.teamboot.domain.Community;
import com.busanit501.teamboot.domain.Member;
import com.busanit501.teamboot.repository.CommentRepository;
import com.busanit501.teamboot.repository.CommunityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentsServiceImpl implements CommentsService {

    private final CommentRepository commentRepository;
    private final CommunityRepository communityRepository;

    @Override
    public List<Comments> getCommentsByCommunityId(Long communityId) {
        return commentRepository.findByCommunityCommunityId(communityId);
    }

    @Override
    public Comments createComment(Long communityId, Member member, String content) {
        // 게시글 조회
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        // 댓글 생성 및 저장
        Comments comments = Comments.builder()
                .community(community)
                .member(member)
                .content(content)
                .build();
        return commentRepository.save(comments);
    }

    @Override
    public void deleteComment(Long commentsId, Member member) {
        Comments comments = commentRepository.findById(commentsId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));
        if (!comments.getMember().getMemberid().equals(member.getMemberid())) {
            throw new SecurityException("삭제 권한이 없습니다.");
        }
        commentRepository.delete(comments);
    }
}
