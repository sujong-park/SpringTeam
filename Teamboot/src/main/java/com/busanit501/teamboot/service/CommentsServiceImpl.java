package com.busanit501.teamboot.service;

import com.busanit501.bootproject.domain.Comments;
import com.busanit501.bootproject.domain.Community;
import com.busanit501.bootproject.domain.Member;
import com.busanit501.bootproject.dto.CommentsDTO;
import com.busanit501.bootproject.dto.PageRequestDTO;
import com.busanit501.bootproject.dto.PageResponseDTO;
import com.busanit501.bootproject.repository.CommentsRepository;
import com.busanit501.bootproject.repository.CommunityRepository;
import com.busanit501.bootproject.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentsServiceImpl implements CommentsService {

    private final CommentsRepository commentsRepository;
    private final CommunityRepository communityRepository; // Community 확인용
    private final MemberRepository memberRepository; // Member 확인용

    @Override
    public Long createComment(CommentsDTO commentsDTO) {
        Community community = communityRepository.findById(commentsDTO.getCommunityId())
                .orElseThrow(() -> new IllegalArgumentException("Community not found"));
        Member member = memberRepository.findById(commentsDTO.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        // 디버깅용 로그
        System.out.println("Community ID: " + community.getCommunityId());
        System.out.println("Member ID: " + member.getMid());
        System.out.println("Content: " + commentsDTO.getContent());


        Comments comment = Comments.builder()
                .community(community)
                .member(member)
                .content(commentsDTO.getContent())
                .build();

        Comments savedComment = commentsRepository.save(comment);
        return savedComment.getCommentsId();
    }

    @Override
    public PageResponseDTO<CommentsDTO> getCommentsByCommunity(Long communityId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size); // 페이지는 0부터 시작
        Page<Comments> result = commentsRepository.findByCommunityCommunityId(communityId, pageable);

        List<CommentsDTO> dtoList = result.getContent().stream()
                .map(CommentsDTO::fromEntity)
                .collect(Collectors.toList());

        // PageRequestDTO 객체 생성
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .page(page)
                .size(size)
                .build();

        // PageResponseDTO 생성 시 PageRequestDTO 전달
        return PageResponseDTO.<CommentsDTO>withAll()
                .dtoList(dtoList)
                .total((int) result.getTotalElements())
                .pageRequestDTO(pageRequestDTO)
                .build();
    }

    @Override
    public void updateComment(Long commentId, String content) {
        Comments comment = commentsRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        comment.updateContent(content);
        commentsRepository.save(comment);
    }

    @Override
    public void deleteComment(Long commentId) {
        commentsRepository.deleteById(commentId);
    }
}
