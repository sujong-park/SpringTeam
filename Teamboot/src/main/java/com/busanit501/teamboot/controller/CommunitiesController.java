package com.busanit501.teamboot.controller;

import com.busanit501.teamboot.domain.Category;
import com.busanit501.teamboot.domain.Community;
import com.busanit501.teamboot.domain.Member;
import com.busanit501.teamboot.dto.CommunityDTO;
import com.busanit501.teamboot.dto.CommunityWithCommentDTO;
import com.busanit501.teamboot.dto.CommentsDTO;
import com.busanit501.teamboot.dto.PageRequestDTO;
import com.busanit501.teamboot.dto.PageResponseDTO;
import com.busanit501.teamboot.repository.MemberRepository;
import com.busanit501.teamboot.service.CommentsService;
import com.busanit501.teamboot.service.CommunityService;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.NoSuchElementException;
import java.util.UUID;

@Log4j2
@Controller
@RequestMapping("/communities")
public class CommunitiesController {

    private final CommunityService communityService;
    private final MemberRepository memberRepository;
    private final CommentsService commentsService;

    public CommunitiesController(CommunityService communityService, MemberRepository memberRepository, CommentsService commentsService) {
        this.communityService = communityService;
        this.memberRepository = memberRepository;
        this.commentsService = commentsService;
    }

    // 게시글 등록 GET
    @GetMapping("/register")
    public String showRegisterForm(
            @AuthenticationPrincipal UserDetails user,
            Model model) {

        model.addAttribute("community", new Community());

        if (user != null) {
            log.info("Logged in user: {}", user.getUsername());
            model.addAttribute("user", user);
        }

        return "communities/register";
    }

    // 게시글 등록 POST
    @PostMapping("/register")
    public String registerCommunity(@RequestParam("title") String title,
                                    @RequestParam("content") String content,
                                    @RequestParam("category") String category,
                                    @RequestParam("file") MultipartFile file,
                                    @AuthenticationPrincipal UserDetails userDetails) {

        log.info("UserDetails: {}", userDetails);
        if (userDetails == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }

        Member member = memberRepository.findByMid(userDetails.getUsername())
                .orElseThrow(() -> new NoSuchElementException("로그인된 사용자가 존재하지 않습니다."));

        String imageUrl = null;
        String uploadDir = "uploads";
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            try {
                Files.createDirectories(uploadPath);
            } catch (IOException ex) {
                throw new RuntimeException("uploads 디렉터리 생성 실패", ex);
            }
        }

        if (!file.isEmpty()) {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            try {
                Files.write(filePath, file.getBytes());
                imageUrl = "/uploads/" + fileName;
            } catch (IOException ex) {
                throw new RuntimeException("파일 업로드 실패", ex);
            }
        }

        Community community = Community.builder()
                .title(title)
                .content(content)
                .category(Category.valueOf(category))
                .member(member)
                .imageUrl(imageUrl)
                .build();

        communityService.createCommunity(community);

        return "redirect:/communities/list";
    }

    // 게시글 목록 조회 (페이징)
    @GetMapping("/list")
    public String listCommunitiesWithList(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails user) {

        log.info("listCommunitiesWithList called with page: {}, size: {}", page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<CommunityWithCommentDTO> communityPage = communityService.getAllCommunity(pageable);

        model.addAttribute("communityPage", communityPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", communityPage.getTotalPages());
        model.addAttribute("prev", communityPage.hasPrevious());
        model.addAttribute("next", communityPage.hasNext());

        if (user != null) {
            log.info("Logged in user: {}", user.getUsername());
            model.addAttribute("user", user);
        }

        return "communities/list";
    }

    // 기타 메서드는 변경 없이 그대로 유지

}
