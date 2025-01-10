package com.busanit501.teamboot.service;

import com.busanit501.teamboot.domain.Category;
import com.busanit501.teamboot.domain.Community;
import com.busanit501.teamboot.domain.Member;
import com.busanit501.teamboot.dto.CommunityDTO;
import com.busanit501.teamboot.dto.CommunityWithCommentDTO;
import com.busanit501.teamboot.repository.CommunityRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommunityServiceImpl implements CommunityService {

    private CommunityRepository communityRepository;

    public CommunityServiceImpl(CommunityRepository communityRepository) {
        this.communityRepository = communityRepository;
    }

    @Override
    public Page<CommunityWithCommentDTO> getAllCommunity(Pageable pageable) {
        Page<Object[]> results = communityRepository.findAllWithMemberAndCommentsCount(pageable);

        return results.map(row -> {
            Community community = (Community) row[0];
            Member member = (Member) row[1];
            Long commentCount = (Long) row[2];

            return new CommunityWithCommentDTO(
                    community.getCommunityId(),
                    community.getTitle(),
                    community.getContent(),
                    community.getCategory(),
                    member != null ? member.getName() : "작성자 없음",  // 사용자 정보 없을 경우 처리
//                    member != null ? member.getAddress() : "주소 미등록",
                    community.getRegDate(),
                    commentCount
            );
        });
    }

    @Override
    public Community getCommunityById(Long id) {
        return communityRepository.findById(id)
                .orElse(null);  // 게시글이 없으면 null 반환
    }

    @Override
    public Community createCommunity(Community community) {
        return communityRepository.save(community);
    }

    @Override
    public Community editCommunity(Long id, Community updatedCommunity) {
        Community existingCommunity = communityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        existingCommunity.updateFromEntity(updatedCommunity);

        return communityRepository.save(existingCommunity);
    }

    @Override
    public void deleteCommunity(Long id) {
        communityRepository.deleteById(id);
    }

    @Override
    public Page<Community> getCommunitiesByCategory(Category category, Pageable pageable) {
        return communityRepository.findByCategory(category, pageable);
    }

}

