package com.busanit501.teamboot.controller;

import com.busanit501.teamboot.domain.Category;
import com.busanit501.teamboot.domain.Community;
import com.busanit501.teamboot.service.CommunityService;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Log4j2
@Controller
@RequestMapping("/communities/useditems")
public class UsedItemsCommunityController {

    private final CommunityService communityService;

    public UsedItemsCommunityController(CommunityService communityService) {
        this.communityService = communityService;
    }

    @GetMapping
    public String listUsedItemsCommunities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        log.info("listUsedItemsCommunities called with page: {}, size: {}", page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<Community> usedItemsCommunities = communityService.getCommunitiesByCategory(Category.USEDITEMS, pageable);

        model.addAttribute("communities", usedItemsCommunities.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", usedItemsCommunities.getTotalPages());

        return "communities/useditems"; // 중고 나눔 게시글 화면
    }
}