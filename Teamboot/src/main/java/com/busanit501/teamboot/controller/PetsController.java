package com.busanit501.teamboot.controller;

import com.busanit501.teamboot.dto.PetDTO;
import com.busanit501.teamboot.dto.PetListAllDTO;
import com.busanit501.teamboot.dto.PageRequestDTO;
import com.busanit501.teamboot.dto.PageResponseDTO;
import com.busanit501.teamboot.service.PetsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Log4j2
@RequestMapping("/pets")
@RequiredArgsConstructor
public class PetsController {
    // 물리 저장소 경로를 불러오기.
    @Value("${com.busanit501.upload.path}")
    private String uploadPath;

    private final PetsService petsService;

    @GetMapping("/")
    public String index() {
        return "redirect:/pets/list";
    }


    @GetMapping("/list")
    public void list(@AuthenticationPrincipal UserDetails user, PageRequestDTO pageRequestDTO, Model model ) {
        PageResponseDTO<PetListAllDTO> responseDTO = petsService.listWithAll(pageRequestDTO);
//        PageResponseDTO<PetDTO> responseDTO = petsService.list(pageRequestDTO);

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
    public String registerPost(@Valid PetDTO petDTO,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {
        log.info("PetController register post 로직처리: ");
        log.info("PetController register post  petDTO : " + petDTO);

        // 유효성 체크 -> 유효성 검증시, 통과 안된 원인이 있다면,
        if (bindingResult.hasErrors()) {
            log.info("has errors : 유효성 에러가 발생함.");
            // 1회용으로, 웹 브라우저에서, errors , 키로 조회 가능함. -> 뷰 ${errors}
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            return "redirect:/pets/register";
        }
        //검사가 통과가 되고, 정상 입력
        Long petId = petsService.register(petDTO);

        // 글 정상 등록후, 화면에 result 값을 전달하기.
        // 1회용 사용하기.
        redirectAttributes.addFlashAttribute("result", petId);
        redirectAttributes.addFlashAttribute("resultType", "register");

        return "redirect:/pets/list";

    }

    @GetMapping("/read")
    public void read(@AuthenticationPrincipal UserDetails user, Long petId, PageRequestDTO pageRequestDTO,
                     Model model) {
        PetDTO petDTO = petsService.readOne(petId);
        model.addAttribute("dto", petDTO);
        model.addAttribute("user", user);
        log.info("user: 정보조회" + user);
        log.info("petDTO: 정보조회" + petDTO);

    }

    @GetMapping("/update")
    public void update(@AuthenticationPrincipal UserDetails user, Long petId, PageRequestDTO pageRequestDTO,
                       Model model) {
        PetDTO petDTO = petsService.readOne(petId);
        model.addAttribute("dto", petDTO);
        model.addAttribute("user", user);
    }
    @PostMapping("/update")
    public String updatePost(@Valid PetDTO petDTO,
                             BindingResult bindingResult,
                             PageRequestDTO pageRequestDTO,
                             String keyword2,String page2, String type2,
                             RedirectAttributes redirectAttributes) {
        log.info("PetController updatePost post 로직처리: ");
        log.info("PetController updatePost post  petDTO : " + petDTO);
        log.info("PetController updatePost post  pageRequestDTO : " + pageRequestDTO);

        //키워드 한글 처리.
        String encodedKeyword = URLEncoder.encode(keyword2, StandardCharsets.UTF_8);

        // 유효성 체크 -> 유효성 검증시, 통과 안된 원인이 있다면,
        if (bindingResult.hasErrors()) {
            log.info("has errors : 유효성 에러가 발생함.");
            // 1회용으로, 웹 브라우저에서, errors , 키로 조회 가능함. -> 뷰 ${errors}
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            return "redirect:/pets/update?petId="+petDTO.getPetId()+"&keyword="+encodedKeyword+"&page="+page2+"&type="+type2;
        }
        //검사가 통과가 되고, 정상 입력
        petsService.update(petDTO);

        // 글 정상 등록후, 화면에 result 값을 전달하기.
        // 1회용 사용하기.
        redirectAttributes.addFlashAttribute("result", petDTO.getPetId());
        redirectAttributes.addFlashAttribute("resultType", "update");

        return "redirect:/pets/read?petId="+petDTO.getPetId()+"&keyword="+encodedKeyword+"&page="+page2+"&type="+type2;

    }

    // @PreAuthorize("principal.username == #petDTO.writer")
    @PostMapping("/delete")
    // 삭제시,
    // 주의사항,
    // 1) 댓글 여부 2) 첨부 이미지, (물리서버, 디비서버 삭제 확인)
    // Long bno -> PetDTO 형식으로 변경할 예정.
    // 첨부 이미지, 물리서버에서 삭제 할려면,
    // 1)물리 서버 경로 필요 2) 실제 삭제 작업.
    public String delete(PetDTO petDTO,
                         String keyword2,String page2, String type2,
                         RedirectAttributes redirectAttributes) {
        Long petId = petDTO.getPetId();
        // 게시글 삭제시, 댓글, 첨부 이미지 삭제, 하지만, 물리 서버는 삭제 안함.
        petsService.delete(petId);

        // 물리 서버에 저장된 이미지 삭제.
        //추가
        List<String> fileNames = petDTO.getFileNames();
        if(fileNames != null && fileNames.size() > 0){
            // uploadController 가져와서 사용한다.
            removeFiles(fileNames);
        }

        //키워드 한글 처리.
        String encodedKeyword = URLEncoder.encode(keyword2, StandardCharsets.UTF_8);

        redirectAttributes.addFlashAttribute("result", petId);
        redirectAttributes.addFlashAttribute("resultType", "delete");
        return "redirect:/pets/list?"+"&keyword="+encodedKeyword+"&page="+page2+"&type="+type2;
    }

    // 물리서버 , 첨부 이미지 삭제 함수.
    public void removeFiles(List<String> fileNames) {
        for (String filename : fileNames) {
            Resource resource = new FileSystemResource(uploadPath+ File.separator+filename);
//            String resourceName = resource.getFilename();

            // 리턴 타입 Map 전달,
            Map<String,Boolean> resultMap = new HashMap<>();
            boolean deleteCheck = false;
            try {
                // 파일 삭제시, 이미지 파일일 경우, 원본 이미지와 , 썸네일 이미지 2개 있어서
                // 이미지 파일 인지 여부를 확인 후, 이미지 이면, 썸네일도 같이 제거해야함.
                String contentType = Files.probeContentType(resource.getFile().toPath());
                // 삭제 여부를 업데이트
                // 원본 파일을 제거하는 기능. (실제 물리 파일 삭제 )
                deleteCheck =resource.getFile().delete();

                if (contentType.startsWith("image")) {
                    // 썸네일 파일을 생성해서, 파일 클래스로 삭제를 진행.
                    // uploadPath : C:\\upload\springTest
                    // File.separator : C:\\upload\springTest\test1.jpg
                    File thumbFile = new File(uploadPath+File.separator,"s_"+ filename);
                    // 실제 물리 파일 삭제
                    thumbFile.delete();
                }
            }
            catch (Exception e) {
                log.error(e.getMessage());
            }
            resultMap.put("result", deleteCheck);
//            return resultMap;
        }
    }


}
