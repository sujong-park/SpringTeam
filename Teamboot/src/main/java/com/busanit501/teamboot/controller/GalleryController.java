package com.busanit501.teamboot.controller;

import com.busanit501.teamboot.dto.*;
import com.busanit501.teamboot.service.GalleryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@Log4j2
@RequestMapping("/gallery")
@RequiredArgsConstructor
public class GalleryController {
    // 물리 저장소 경로를 불러오기.
    @Value("${com.busanit501.upload.path}")
    private String uploadPath;

    private final GalleryService galleryService;

    @GetMapping("/list")
    public void list(@AuthenticationPrincipal UserDetails user, PageRequestDTO pageRequestDTO, Model model ) {
        PageResponseDTO<GalleryListAllDTO> responseDTO = galleryService.listWithAll(pageRequestDTO);
//        PageResponseDTO<GalleryDTO> responseDTO = galleryService.list(pageRequestDTO);

        // user 정보를 화면에 전달하기.
        model.addAttribute("user", user);
        model.addAttribute("responseDTO", responseDTO);

    }

    @GetMapping("/register")
    public void register(@AuthenticationPrincipal UserDetails user, Model model) {
        model.addAttribute("user", user);

    }
    @PostMapping("/register")
    // 일반글로 만 받을 때, DTO 클래스로 받고 있는데,
    // 화면에서, -> 파일 이미지들을 문자열 형태로 , 각각 따로 보내고 있음.
    // 받을 때 타입을 변경.
    public String registerPost(@Valid GalleryDTO galleryDTO,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {
        log.info("GalleryController register post 로직처리: ");
        log.info("GalleryController register post  boardDTO : " + galleryDTO);

        // 유효성 체크 -> 유효성 검증시, 통과 안된 원인이 있다면,
        if (bindingResult.hasErrors()) {
            log.info("has errors : 유효성 에러가 발생함.");
            // 1회용으로, 웹 브라우저에서, errors , 키로 조회 가능함. -> 뷰 ${errors}
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            return "redirect:/board/register";
        }
        //검사가 통과가 되고, 정상 입력
        Long galleryId = galleryService.register(galleryDTO);

        // 글 정상 등록후, 화면에 result 값을 전달하기.
        // 1회용 사용하기.
        redirectAttributes.addFlashAttribute("result", galleryId);
        redirectAttributes.addFlashAttribute("resultType", "register");

        return "redirect:/gallery/list";

    }

    @GetMapping("/read")
    public void read(@AuthenticationPrincipal UserDetails user, Long galleryId, PageRequestDTO pageRequestDTO,
                     Model model) {
        GalleryDTO galleryDTO = galleryService.readOne(galleryId);
        model.addAttribute("dto", galleryDTO);
        model.addAttribute("user", user);
        log.info("user: 정보조회" + user);
        log.info("boardDTO: 정보조회" + galleryDTO);

    }

    @GetMapping("/update")
    public void update(Long galleryId, PageRequestDTO pageRequestDTO,
                       Model model) {
        GalleryDTO galleryDTO = galleryService.readOne(galleryId);
        model.addAttribute("dto", galleryDTO);
    }
    @PostMapping("/update")
    public String updatePost(@Valid GalleryDTO galleryDTO,
                             BindingResult bindingResult,
                             PageRequestDTO pageRequestDTO,
                             String keyword2,String page2, String type2,
                             RedirectAttributes redirectAttributes) {
        log.info("GalleryController updatePost post 로직처리: ");
        log.info("GalleryController updatePost post  galleryDTO : " + galleryDTO);

        log.info("GalleryController updatePost post  pageRequestDTO : " + pageRequestDTO);

        //키워드 한글 처리.
        String encodedKeyword = URLEncoder.encode(keyword2, StandardCharsets.UTF_8);

        // 유효성 체크 -> 유효성 검증시, 통과 안된 원인이 있다면,
        if (bindingResult.hasErrors()) {
            log.info("has errors : 유효성 에러가 발생함.");
            // 1회용으로, 웹 브라우저에서, errors , 키로 조회 가능함. -> 뷰 ${errors}
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            return "redirect:/gallery/update?galleryId="+galleryDTO.getGalleryId()+"&keyword="+encodedKeyword+"&page="+page2+"&type="+type2;
        }
        //검사가 통과가 되고, 정상 입력
        galleryService.update(galleryDTO);

        // 글 정상 등록후, 화면에 result 값을 전달하기.
        // 1회용 사용하기.
        redirectAttributes.addFlashAttribute("result", galleryDTO.getGalleryId());
        redirectAttributes.addFlashAttribute("resultType", "update");

        return "redirect:/gallery/read?galleryId="+galleryDTO.getGalleryId()+"&keyword="+encodedKeyword+"&page="+page2+"&type="+type2;

    }


}
