package com.busanit501.teamboot.service;

import com.busanit501.bootproject.domain.Category;
import com.busanit501.bootproject.domain.Community;
import com.busanit501.bootproject.dto.CommunityWithCommentDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface CommunityService {
    Page<CommunityWithCommentDTO> getAllCommunity(Pageable pageable);
    Community getCommunityById(Long id);
    Community createCommunity(Community community);
    Community editCommunity(Long id, Community community);
    void deleteCommunity(Long id);
    Page<Community> getCommunitiesByCategory(Category category, Pageable pageable);
}
