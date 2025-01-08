package com.busanit501.teamboot.controller;

import com.busanit501.teamboot.domain.Category;
import com.busanit501.teamboot.domain.Comments;
import com.busanit501.teamboot.domain.Community;
import com.busanit501.teamboot.domain.Member;
import com.busanit501.teamboot.dto.CommunityDTO;
import com.busanit501.teamboot.dto.CommunityWithCommentDTO;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
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

    // 📌 게시글 등록 GET
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("community", new Community());
        return "communities/register";
    }

    // 📌 게시글 등록 POST
    @PostMapping("/register")
    public String registerCommunity(@RequestParam("title") String title,
                                    @RequestParam("content") String content,
                                    @RequestParam("category") String category,
                                    @RequestParam("file") MultipartFile file,
                                    @AuthenticationPrincipal UserDetails userDetails) {

        // 현재 로그인된 사용자 정보 불러오기
        Member member = memberRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new NoSuchElementException("로그인된 사용자가 존재하지 않습니다."));

        // 기본 이미지 URL 설정
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

        // 파일 업로드 처리
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
        return "redirect:/communities";
    }

    // 📌 게시글 목록 조회 (페이징)
    @GetMapping
    public String listCommunities(Model model,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CommunityWithCommentDTO> communityPage = communityService.getAllCommunity(pageable);

        model.addAttribute("communityPage", communityPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", communityPage.getTotalPages());
        return "communities/list";
    }

    // 📌 게시글 상세 조회 (댓글 포함)
    @GetMapping("/{id}")
    public String getCommunityDetail(@PathVariable Long id, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Community community = communityService.getCommunityById(id);
        List<Comments> comments = commentsService.getCommentsByCommunityId(id);

        String loggedInUserId = null;
        if (userDetails != null) {
            loggedInUserId = userDetails.getUsername();
        }

        model.addAttribute("community", community);
        model.addAttribute("comments", comments);
        model.addAttribute("loggedInUserId", loggedInUserId);

        return "communities/detail";
    }

    // 📌 게시글 수정 페이지
    @GetMapping("/edit/{id}")
    public String editCommunityForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Community community = communityService.getCommunityById(id);

        if (community == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "수정할 게시글이 존재하지 않습니다.");
            return "redirect:/communities";
        }

        model.addAttribute("community", community);
        return "communities/edit";
    }

    // 📌 게시글 수정 처리
    @PostMapping("/edit/{id}")
    public String editCommunity(@PathVariable Long id,
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
        existingCommunity.setImageUrl(imageUrl);
        communityService.editCommunity(id, existingCommunity);

        return "redirect:/communities";
    }

    // 📌 게시글 삭제 처리
    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public String deleteCommunity(@PathVariable Long id) {
        communityService.deleteCommunity(id);
        return "success";
    }
}
