package com.busanit501.teamboot.service;

import com.busanit501.teamboot.domain.Category;
import com.busanit501.teamboot.domain.Community;
import com.busanit501.teamboot.dto.CommunityWithCommentDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface CommunityService {
    Page<CommunityWithCommentDTO> getAllCommunity(Pageable pageable);
    Community getCommunityById(Long id);
    Community createCommunity(Community community);
    Community editCommunity(Long id, Community community);
    void deleteCommunity(Long id);
    Page<Community> getCommunitiesByCategory(Category category, Pageable pageable);
}
