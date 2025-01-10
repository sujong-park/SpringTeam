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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
    public String showRegisterForm(Model model) {
        model.addAttribute("community", new Community());
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
    public String listCommunitiesWithList(Model model,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size) {
        log.info("listCommunitiesWithList called with page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<CommunityWithCommentDTO> communityPage = communityService.getAllCommunity(pageable);

        model.addAttribute("communityPage", communityPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", communityPage.getTotalPages());
        return "communities/list";
    }

    // 게시글 상세 조회 (댓글 포함)
    @GetMapping("/{id}")
    public String getCommunityDetail(@PathVariable Long id, Model model,
                                     @AuthenticationPrincipal UserDetails userDetails) {
        // 게시글 가져오기
        Community community = communityService.getCommunityById(id);

        // 댓글 가져오기 (기본 페이지 및 크기 설정)
        int page = 1; // 기본값
        int size = 10; // 기본값
        PageResponseDTO<CommentsDTO> comments = commentsService.getCommentsByCommunity(id, page, size);

        // 로그인한 사용자 ID 가져오기
        String loggedInUserId = null;
        if (userDetails != null) {
            loggedInUserId = userDetails.getUsername(); // UserDetails의 username은 ID로 매핑
        }

        // Thymeleaf로 데이터 전달
        model.addAttribute("comments", comments.getDtoList());
        model.addAttribute("community", community);
        model.addAttribute("loggedInUserId", loggedInUserId);

        return "communities/read";
    }

    // 게시글 수정 페이지
    @GetMapping("/update/{id}")
    public String editCommunityForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Community community = communityService.getCommunityById(id);

        if (community == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "수정할 게시글이 존재하지 않습니다.");
            return "redirect:/communities";
        }

        model.addAttribute("community", community);
        return "communities/update";
    }

    // 게시글 수정 처리
    @PostMapping("/update/{id}")
    @ResponseBody
    public String editCommunity(
            @PathVariable Long id,
            @ModelAttribute CommunityDTO communityDTO,
            @RequestParam(value = "file", required = false) MultipartFile file) {

        Community existingCommunity = communityService.getCommunityById(id);

        String imageUrl = existingCommunity.getImageUrl();
        if (file != null && !file.isEmpty()) {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path uploadPath = Paths.get("uploads");

            try {
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                Path filePath = uploadPath.resolve(fileName);
                Files.write(filePath, file.getBytes());
                imageUrl = "/uploads/" + fileName;
            } catch (IOException ex) {
                throw new RuntimeException("파일 업로드 실패", ex);
            }
        }

        existingCommunity.updateFromDTO(communityDTO);
        existingCommunity.updateImageUrl(imageUrl);
        communityService.editCommunity(id, existingCommunity);

        return "success";
    }

    // 게시글 삭제 처리
    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public String deleteCommunity(@PathVariable Long id) {
        communityService.deleteCommunity(id);
        return "success";
    }
}