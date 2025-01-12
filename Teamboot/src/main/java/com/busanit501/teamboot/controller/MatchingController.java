package com.busanit501.teamboot.controller;

import com.busanit501.teamboot.domain.*;
import com.busanit501.teamboot.dto.MatchingRoomDTO;
import com.busanit501.teamboot.exception.ResourceNotFoundException;
import com.busanit501.teamboot.repository.MemberRepository;
import com.busanit501.teamboot.service.MatchingCalendarService;
import com.busanit501.teamboot.service.MatchingService;
import com.busanit501.teamboot.service.PetService;
import com.nimbusds.oauth2.sdk.GeneralException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Log4j2
@Controller
@RequestMapping("/matching")
@RequiredArgsConstructor
public class MatchingController {

    private final MatchingService matchingService;
    private final PetService petService;
    private final MatchingCalendarService matchingCalendarService;
    private final MemberRepository memberRepository;

    // 매칭 리스트 조회
    @GetMapping("/list")
    public String list(Model model,
                       @RequestParam(value = "query", required = false) String query,
                       @AuthenticationPrincipal UserDetails userDetails,
                       RedirectAttributes redirectAttributes) throws GeneralException {

        Member loginMember = getAuthenticatedMember(userDetails, redirectAttributes, "redirect:/member/login");

        // 모든 매칭방 리스트 가져오기
        List<MatchingRoomDTO> allRooms = matchingService.getAllRooms();

        // 검색어가 있을 경우 필터링된 매칭방 리스트 가져오기
        List<MatchingRoomDTO> filteredRooms = Collections.emptyList();
        if (query != null && !query.trim().isEmpty()) {
            filteredRooms = matchingService.getRoomsByQuery(query);
        }

        // 상위 5개의 키워드
        List<String> topKeywords = matchingService.getTopKeywords(5);

        // 모델에 데이터 추가
        model.addAttribute("allRooms", allRooms); // 모든 방
        model.addAttribute("filteredRooms", filteredRooms); // 검색된 방
        model.addAttribute("keywords", topKeywords); // 인기 키워드
        model.addAttribute("query", query); // 검색어 유지
        model.addAttribute("user", userDetails);

        return "matching/list";
    }

    // 매칭방 생성 폼
    @GetMapping("/create")
    public String createForm(@AuthenticationPrincipal UserDetails userDetails, Model model, RedirectAttributes redirectAttributes) throws GeneralException {
        Member loginMember = getAuthenticatedMember(userDetails, redirectAttributes, "redirect:/member/login");

        List<Pet> userPets = petService.findAllBymId(loginMember.getMid());
        model.addAttribute("userPets", userPets);

        MatchingRoomDTO dto = new MatchingRoomDTO();
        dto.setMeetingDate(LocalDate.now());
        dto.setMeetingTime(LocalTime.now().withSecond(0).withNano(0));
        dto.setMaxParticipants(4L);
        model.addAttribute("matchingRoomDTO", dto);
        model.addAttribute("user", userDetails);
        return "matching/create";
    }

    // 매칭방 생성 처리
    @PostMapping("/create")
    public String createSubmit(@Valid @ModelAttribute("matchingRoomDTO") MatchingRoomDTO dto,
                               BindingResult bindingResult,
                               @RequestParam(name = "imageFile", required = false) MultipartFile imageFile,
                               @AuthenticationPrincipal UserDetails userDetails,
                               Model model,
                               RedirectAttributes redirectAttributes) throws GeneralException {
        Member loginMember = getAuthenticatedMember(userDetails, redirectAttributes, "redirect:/member/login");

        if (bindingResult.hasErrors()) {
            List<Pet> userPets = petService.findAllBymId(loginMember.getMid());
            model.addAttribute("userPets", userPets);
            model.addAttribute("user", userDetails);
            return "matching/create";
        }

        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                String originalFilename = imageFile.getOriginalFilename();
                String uniqueFileName = UUID.randomUUID().toString() + "_" + originalFilename;
                String uploadDir = "C:/upload/"; // 실제 서버 환경에 맞게 수정
                File uploadDirectory = new File(uploadDir);
                if (!uploadDirectory.exists()) {
                    uploadDirectory.mkdirs(); // 디렉토리 생성
                }

                File destinationFile = new File(uploadDirectory, uniqueFileName);
                imageFile.transferTo(destinationFile);

                String savedProfilePicture = "/upload/" + uniqueFileName;
                dto.setProfilePicture(savedProfilePicture);
            }

            matchingService.createRoom(dto, loginMember);
            redirectAttributes.addFlashAttribute("successMessage", "매칭방이 성공적으로 생성되었습니다.");
            return "redirect:/matching/list";

        } catch (RuntimeException | IOException e) {
            throw new GeneralException("매칭방 생성 중 오류가 발생했습니다.", e);
        }

    }

    // 매칭방 상세 조회
    @GetMapping("/detail/{id}")
    public String getMatchingRoomDetail(@PathVariable("id") Long roomId,
                                        Model model,
                                        @AuthenticationPrincipal UserDetails userDetails,
                                        RedirectAttributes redirectAttributes) throws GeneralException {
        Member loginMember = getAuthenticatedMember(userDetails, redirectAttributes, "redirect:/member/login");
        model.addAttribute("user", userDetails);
        try {
            MatchingRoom room = matchingService.getRoomById(roomId);
            List<RoomParticipant> participants = matchingService.getParticipantsByRoomId(roomId);

            // 대기(Pending)/승인(Accepted) 구분
            List<RoomParticipant> pending = matchingService.filterParticipants(
                    participants, RoomParticipant.ParticipantStatus.Pending);
            List<RoomParticipant> accepted = matchingService.filterParticipants(
                    participants, RoomParticipant.ParticipantStatus.Accepted);

            // 호스트 여부
            boolean isHost = room.getMember().getMid().equals(loginMember.getMid());

            // pendingMap
            Map<Member, List<Pet>> pendingMap = matchingService.getPendingMemberPets(room);
            model.addAttribute("pendingMap", pendingMap);

            // acceptedMap 만들기 (Map<Member, List<Pet>>)
            Map<Member, List<Pet>> acceptedMap = matchingService.getAcceptedMemberPets(room);
            if (acceptedMap == null) {
                acceptedMap = new HashMap<>(); // ★ null 방지
            }

            // 모델에 담기
            model.addAttribute("room", matchingService.convertToDto(room));
            model.addAttribute("pendingParticipants", pending);
            model.addAttribute("acceptedParticipants", accepted);
            model.addAttribute("acceptedMap", acceptedMap);
            model.addAttribute("isHost", isHost);
            log.info("Accepted map size: {}", acceptedMap.size());

            // 호스트가 아닌 경우만 userPets 준비 (유저가 신청 모달에서 선택)
            if (!isHost) {
                List<Pet> userPets = petService.findAllBymId(loginMember.getMid());
                model.addAttribute("userPets", userPets);

                // 사용자가 이미 신청했는지 여부 확인
                boolean hasApplied = matchingService.hasMemberApplied(room, loginMember);
                model.addAttribute("hasApplied", hasApplied);
            }

            return "matching/detail";
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException(e.getMessage(), e);
        }
    }

    // 매칭방 수정 폼
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable("id") Long roomId,
                           Model model,
                           @AuthenticationPrincipal UserDetails userDetails,
                           RedirectAttributes redirectAttributes) throws GeneralException, AccessDeniedException {
        Member loginMember = getAuthenticatedMember(userDetails, redirectAttributes, "redirect:/member/login");
        model.addAttribute("user", userDetails);
        try {
            MatchingRoom room = matchingService.getRoomById(roomId);
            if (!room.getMember().getMid().equals(loginMember.getMid())) {
                throw new AccessDeniedException("방장만 수정할 수 있습니다.");
            }

            MatchingRoomDTO dto = matchingService.convertToDto(room);
            dto.setRoomId(roomId);

            List<Pet> userPets = petService.findAllBymId(loginMember.getMid());
            model.addAttribute("matchingRoomDTO", dto);
            model.addAttribute("userPets", userPets);
            return "matching/edit";
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException(e.getMessage(), e);
        } catch (AccessDeniedException e) {
            throw new AccessDeniedException(e.getMessage());
        }
    }

    // 매칭방 수정 처리
    @PostMapping("/edit/{id}")
    public String editSubmit(@PathVariable("id") Long roomId,
                             @Valid @ModelAttribute("matchingRoomDTO") MatchingRoomDTO dto,
                             BindingResult bindingResult,
                             @RequestParam(name = "imageFile", required = false) MultipartFile imageFile,
                             @AuthenticationPrincipal UserDetails userDetails,
                             Model model,
                             RedirectAttributes redirectAttributes) throws GeneralException {
        Member loginMember = getAuthenticatedMember(userDetails, redirectAttributes, "redirect:/member/login");

        if (bindingResult.hasErrors()) {
            List<Pet> userPets = petService.findAllBymId(loginMember.getMid());
            model.addAttribute("userPets", userPets);
            return "matching/edit";
        }

        try {
            // 이미지 파일 업로드 처리
            if (imageFile != null && !imageFile.isEmpty()) {
                String originalFilename = imageFile.getOriginalFilename();
                String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

                // 업로드 디렉토리 경로 설정
                String uploadDir = "C:/upload/";
                File uploadDirectory = new File(uploadDir);
                if (!uploadDirectory.exists()) {
                    uploadDirectory.mkdirs(); // 디렉토리 생성
                }

                // 파일 저장
                File destinationFile = new File(uploadDirectory, uniqueFileName);
                imageFile.transferTo(destinationFile);

                // 저장된 파일의 URL 설정
                String savedProfilePicture = "/upload/" + uniqueFileName;
                dto.setProfilePicture(savedProfilePicture);
            }

            // 매칭방 정보 업데이트
            matchingService.updateRoom(roomId, dto, loginMember);

            redirectAttributes.addFlashAttribute("successMessage", "매칭방이 성공적으로 수정되었습니다.");
            return "redirect:/matching/detail/" + roomId;

        } catch (Exception e) {
            throw new GeneralException("매칭방 수정 중 오류가 발생했습니다.", e);
        }
    }

    // 매칭방 삭제
    @PostMapping("/delete/{id}")
    public String deleteRoom(@PathVariable Long id,
                             @AuthenticationPrincipal UserDetails userDetails,
                             RedirectAttributes redirectAttributes) throws GeneralException, AccessDeniedException {
        Member loginMember = getAuthenticatedMember(userDetails, redirectAttributes, "redirect:/member/login");

        try {
            MatchingRoom room = matchingService.getRoomById(id);
            if (!room.getMember().getMid().equals(loginMember.getMid())) {
                throw new AccessDeniedException("방장만 삭제할 수 있습니다.");
            }

            matchingService.deleteRoom(id);
            redirectAttributes.addFlashAttribute("deleteSuccessMessage", "매칭방이 성공적으로 삭제되었습니다.");
            return "redirect:/matching/list";
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException(e.getMessage(), e);
        } catch (AccessDeniedException e) {
            throw new AccessDeniedException(e.getMessage());
        } catch (Exception ex) {
            throw new GeneralException("매칭방 삭제 중 문제가 발생했습니다.", ex);
        }
    }

    // 스케줄 확정
    @PostMapping("/confirm/{id}")
    public String confirmSchedule(@PathVariable Long id,
                                  @AuthenticationPrincipal UserDetails userDetails,
                                  RedirectAttributes redirectAttributes) throws GeneralException {
        log.info("Confirm request received for roomId: {}", id);
        Member loginMember = getAuthenticatedMember(userDetails, redirectAttributes, "redirect:/member/login");

        try {
            MatchingRoom room = matchingService.getRoomById(id);
            List<Member> participants = matchingService.getAcceptedParticipantsByRoomId(id);

            matchingCalendarService.saveSchedule(loginMember, room, participants);
            redirectAttributes.addFlashAttribute("successMessage", "스케줄이 확정되었습니다.");

            return "redirect:/matching/list";
        } catch (Exception ex) {
            throw new GeneralException("스케줄 확정 중 문제가 발생했습니다.", ex);
        }
    }

    // 매칭방 신청
    @PostMapping("/apply/{roomId}")
    public String applyRoom(@PathVariable("roomId") Long roomId,
                            @RequestParam(value = "additionalPetIds", required = false) List<Long> additionalPetIds,
                            @AuthenticationPrincipal UserDetails userDetails,
                            RedirectAttributes redirectAttributes) throws GeneralException {
        Member loginMember = getAuthenticatedMember(userDetails, redirectAttributes, "redirect:/member/login");

        try {
            log.info("Member {} is applying to room {}", loginMember.getMid(), roomId);
            log.info("Selected pet IDs: {}", additionalPetIds);
            matchingService.applyRoom(roomId, loginMember.getMid(), additionalPetIds);
            redirectAttributes.addFlashAttribute("successMessage", "참가 신청이 성공적으로 완료되었습니다.");
            return "redirect:/matching/detail/" + roomId;
        } catch (RuntimeException e) {
            throw new GeneralException("참가 신청 중 오류가 발생했습니다.", e);
        }
    }

    // 참가자 승인
    @PostMapping("/accept/{roomId}/{memberId}")
    public String acceptParticipant(@PathVariable("roomId") Long roomId,
                                    @PathVariable("memberId") String memberId,
                                    @AuthenticationPrincipal UserDetails userDetails,
                                    RedirectAttributes redirectAttributes) throws GeneralException {
        Member loginMember = getAuthenticatedMember(userDetails, redirectAttributes, "redirect:/member/login");

        try {
            matchingService.acceptParticipant(roomId, memberId);
            redirectAttributes.addFlashAttribute("successMessage", "참가자가 승인되었습니다.");
            return "redirect:/matching/detail/" + roomId;
        } catch (RuntimeException e) {
            throw new GeneralException("참가자 승인 중 오류가 발생했습니다.", e);
        }
    }

    // 참가자 거절
    @PostMapping("/reject/{roomId}/{memberId}")
    public String rejectParticipant(@PathVariable("roomId") Long roomId,
                                    @PathVariable("memberId") String memberId,
                                    @AuthenticationPrincipal UserDetails userDetails,
                                    RedirectAttributes redirectAttributes) throws GeneralException {
        Member loginMember = getAuthenticatedMember(userDetails, redirectAttributes, "redirect:/member/login");

        try {
            matchingService.rejectParticipant(roomId, memberId);
            redirectAttributes.addFlashAttribute("successMessage", "참가자가 거절되었습니다.");
            return "redirect:/matching/detail/" + roomId;
        } catch (RuntimeException e) {
            throw new GeneralException("참가자 거절 중 오류가 발생했습니다.", e);
        }
    }

    // 인증된 사용자 조회 메소드
    private Member getAuthenticatedMember(UserDetails userDetails, RedirectAttributes redirectAttributes, String redirectUrl) throws GeneralException {
        if (userDetails == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "로그인이 필요합니다.");
            throw new GeneralException("로그인이 필요합니다.");
        }

        return memberRepository.findByMid(userDetails.getUsername())
                .orElseThrow(() -> new GeneralException("사용자 정보를 찾을 수 없습니다."));
    }
}
