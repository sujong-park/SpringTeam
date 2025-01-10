package com.busanit501.teamboot.controller;

import com.busanit501.teamboot.domain.Category;
import com.busanit501.teamboot.domain.Community;
import com.busanit501.teamboot.service.CommunityService;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Log4j2
@Controller
@RequestMapping("/communities/hospital")
public class HospitalCommunityController {

    private final CommunityService communityService;

    public HospitalCommunityController(CommunityService communityService) {
        this.communityService = communityService;
    }

    @GetMapping
    public String listHospitalCommunities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model,
            @AuthenticationPrincipal UserDetails user) {

        log.info("listHospitalCommunities called with page: {}, size: {}", page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<Community> hospitalCommunities = communityService.getCommunitiesByCategory(Category.HOSPITAL, pageable);

        model.addAttribute("communities", hospitalCommunities.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", hospitalCommunities.getTotalPages());

        if (user != null) {
            log.info("Logged in user: {}", user.getUsername());
            model.addAttribute("user", user);
        }

        return "communities/hospital"; // 병원 게시글 화면
    }
}
