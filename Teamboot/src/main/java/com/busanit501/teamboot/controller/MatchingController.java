package com.busanit501.teamboot.controller;

import com.busanit501.teamboot.domain.MatchingRoom;
import com.busanit501.teamboot.domain.Member;
import com.busanit501.teamboot.domain.Pet;
import com.busanit501.teamboot.domain.RoomParticipant;
import com.busanit501.teamboot.dto.MatchingRoomDTO;
import com.busanit501.teamboot.exception.ResourceNotFoundException;
import com.busanit501.teamboot.repository.MemberRepository;
import com.busanit501.teamboot.service.CalendarService;
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

/**
 * 매칭 컨트롤러 클래스
 * 매칭방 관련 HTTP 요청을 처리합니다.
 */
@Log4j2
@Controller
@RequestMapping("/matching")
@RequiredArgsConstructor
public class MatchingController {

    private final MatchingService matchingService;
    private final PetService petService;
    private final CalendarService calendarService;
    private final MemberRepository memberRepository;

    // 리스트가 생성이 안되서 임의로 넣음.
    @GetMapping("/read")
    public void read(@AuthenticationPrincipal UserDetails user, Model model) {
        model.addAttribute("user", user);
    }

    /**
     * 매칭방 리스트 조회 페이지
     *
     * @param model               모델 객체
     * @param query               검색어 (선택적)
     * @param userDetails         인증된 사용자 정보
     * @param redirectAttributes  리다이렉트 시 속성
     * @return 매칭방 리스트 페이지 뷰 이름
     * @throws GeneralException 일반 예외 발생 시
     */
    @GetMapping("/list")
    public String list(Model model,
                       @RequestParam(value = "query", required = false) String query,
                       @AuthenticationPrincipal UserDetails userDetails,
                       RedirectAttributes redirectAttributes) throws GeneralException {

        // 로그인된 사용자 정보 확인
        if (userDetails == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "로그인이 필요합니다.");
            return "redirect:/member/login";
        }

        // 현재 로그인한 사용자의 정보를 가져와 멤버 조회
        Member loginMember = memberRepository.findByMid(userDetails.getUsername())
                .orElseThrow(() -> new GeneralException("사용자 정보를 찾을 수 없습니다."));

        log.info("로그인 사용자: {}", loginMember.getName());

        // 로그인된 사용자 정보 추가
        model.addAttribute("user", userDetails);

        // 모든 매칭방 리스트 가져오기
        List<MatchingRoomDTO> allRooms = matchingService.getAllRooms();

        // 검색어가 있을 경우 필터링된 매칭방 리스트 가져오기
        List<MatchingRoomDTO> filteredRooms = Collections.emptyList();
        if (query != null && !query.trim().isEmpty()) {
            filteredRooms = matchingService.getRoomsByQuery(query);
            log.info("검색어: {}, 검색된 매칭방 수: {}", query, filteredRooms.size());
        }

        // 상위 5개의 키워드
        List<String> topKeywords = matchingService.getTopKeywords(5);
        log.info("상위 키워드: {}", topKeywords);

        // 모델에 데이터 추가
        model.addAttribute("allRooms", allRooms); // 모든 방
        model.addAttribute("filteredRooms", filteredRooms); // 검색된 방
        model.addAttribute("keywords", topKeywords); // 인기 키워드
        model.addAttribute("query", query); // 검색어 유지
        model.addAttribute("member", loginMember);

        return "matching/list";
    }

    /**
     * 매칭방 생성 폼 페이지
     *
     * @param userDetails         인증된 사용자 정보
     * @param model               모델 객체
     * @param redirectAttributes  리다이렉트 시 속성
     * @return 매칭방 생성 폼 페이지 뷰 이름
     * @throws GeneralException 일반 예외 발생 시
     */
    @GetMapping("/create")
    public String createForm(@AuthenticationPrincipal UserDetails userDetails, Model model, RedirectAttributes redirectAttributes) throws GeneralException {
        // 로그인된 사용자 정보 확인
        if (userDetails == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "로그인이 필요합니다.");
            return "redirect:/member/login";
        }

        // 현재 로그인한 사용자의 정보를 가져와 멤버 조회
        Member loginMember = memberRepository.findByMid(userDetails.getUsername())
                .orElseThrow(() -> new GeneralException("사용자 정보를 찾을 수 없습니다."));

        // 사용자 펫 목록을 가져와 모델에 추가
        List<Pet> memberPets = petService.findAllBymId(loginMember.getMid());
        model.addAttribute("memberPets", memberPets);

        // MatchingRoomDTO 초기화
        MatchingRoomDTO dto = new MatchingRoomDTO();
        dto.setMeetingDate(LocalDate.now());
        dto.setMeetingTime(LocalTime.now().withSecond(0).withNano(0));
        dto.setMaxParticipants(4L);
        model.addAttribute("matchingRoomDTO", dto);

        // 로그인된 사용자 정보 추가
        model.addAttribute("user", userDetails);

        return "matching/create";
    }

    /**
     * 매칭방 생성 처리 메서드
     *
     * @param dto                매칭방 DTO
     * @param bindingResult      바인딩 결과
     * @param imageFile          업로드된 이미지 파일 (선택적)
     * @param userDetails        인증된 사용자 정보
     * @param model              모델 객체
     * @param redirectAttributes 리다이렉트 시 속성
     * @return 매칭방 생성 결과에 따른 리다이렉트 URL
     * @throws GeneralException 일반 예외 발생 시
     */
    @PostMapping("/create")
    public String createSubmit(@Valid @ModelAttribute("matchingRoomDTO") MatchingRoomDTO dto,
                               BindingResult bindingResult,
                               @RequestParam(name = "imageFile", required = false) MultipartFile imageFile,
                               @AuthenticationPrincipal UserDetails userDetails,
                               Model model,
                               RedirectAttributes redirectAttributes) throws GeneralException {
        // 로그인된 사용자 정보 확인
        if (userDetails == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "로그인이 필요합니다.");
            return "redirect:/member/login";
        }

        // 현재 로그인한 사용자의 정보를 가져와 멤버 조회
        Member loginMember = memberRepository.findByMid(userDetails.getUsername())
                .orElseThrow(() -> new GeneralException("사용자 정보를 찾을 수 없습니다."));

        // 사용자 펫 목록을 가져와 모델에 추가
        List<Pet> memberPets = petService.findAllBymId(loginMember.getMid());
        model.addAttribute("memberPets", memberPets);

        // 로그인된 사용자 정보 추가
        model.addAttribute("user", userDetails);

        // 유효성 검사 오류가 있는 경우
        if (bindingResult.hasErrors()) {
            return "matching/create";
        }

        try {
            // 이미지 파일 업로드 처리
            if (imageFile != null && !imageFile.isEmpty()) {
                String originalFilename = imageFile.getOriginalFilename();
                String uniqueFileName = UUID.randomUUID().toString() + "_" + originalFilename;
                String uploadDir = "C:/upload/"; // 실제 서버 환경에 맞게 수정
                File uploadDirectory = new File(uploadDir);
                if (!uploadDirectory.exists()) {
                    uploadDirectory.mkdirs(); // 디렉토리 생성
                }

                File destinationFile = new File(uploadDirectory, uniqueFileName);
                imageFile.transferTo(destinationFile); // 파일 저장

                String savedProfilePicture = "/upload/" + uniqueFileName;
                dto.setProfilePicture(savedProfilePicture); // 저장된 이미지 URL 설정
            }

            // 매칭방 생성 서비스 호출
            matchingService.createRoom(dto, loginMember);
            redirectAttributes.addFlashAttribute("successMessage", "매칭방이 성공적으로 생성되었습니다.");
            return "redirect:/matching/list";

        } catch (RuntimeException | IOException e) {
            throw new GeneralException("매칭방 생성 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 매칭방 상세 조회 페이지
     *
     * @param roomId             매칭방 ID
     * @param model              모델 객체
     * @param userDetails        인증된 사용자 정보
     * @param redirectAttributes 리다이렉트 시 속성
     * @return 매칭방 상세 조회 페이지 뷰 이름
     * @throws GeneralException 일반 예외 발생 시
     */
    @GetMapping("/detail/{id}")
    public String getMatchingRoomDetail(@PathVariable("id") Long roomId,
                                        Model model,
                                        @AuthenticationPrincipal UserDetails userDetails,
                                        RedirectAttributes redirectAttributes) throws GeneralException {
        log.info("요청된 매칭방 상세보기 - roomId: {}", roomId);

        // 로그인된 사용자 정보 확인
        if (userDetails == null) {
            log.warn("로그인되지 않은 사용자 요청 - roomId: {}", roomId);
            redirectAttributes.addFlashAttribute("errorMessage", "로그인이 필요합니다.");
            return "redirect:/member/login";
        }

        log.info("로그인된 사용자 확인: {}", userDetails.getUsername());

        // 현재 로그인한 사용자의 정보를 가져와 멤버 조회
        Member loginMember = memberRepository.findByMid(userDetails.getUsername())
                .orElseThrow(() -> new GeneralException("사용자 정보를 찾을 수 없습니다."));

        log.info("로그인 사용자 정보 - mid: {}, name: {}", loginMember.getMid(), loginMember.getName());

        // 사용자 펫 목록을 가져와 모델에 추가
        List<Pet> memberPets = petService.findAllBymId(loginMember.getMid());
        log.info("로그인 사용자의 펫 수: {}", memberPets.size());
        model.addAttribute("memberPets", memberPets);

        // 로그인된 사용자 정보 추가
        model.addAttribute("user", userDetails);

        try {
            // 매칭방 정보 조회
            MatchingRoom room = matchingService.getRoomById(roomId);
            log.info("매칭방 정보 조회 완료 - roomId: {}, title: {}", room.getRoomId(), room.getTitle());

            // 참가자 정보 조회
            List<RoomParticipant> participants = matchingService.getParticipantsByRoomId(roomId);
            log.info("매칭방 참가자 수 - roomId: {}, count: {}", roomId, participants.size());

            // 대기(Pending)/승인(Accepted) 상태로 분류
            List<RoomParticipant> pending = matchingService.filterParticipants(
                    participants, RoomParticipant.ParticipantStatus.Pending);
            List<RoomParticipant> accepted = matchingService.filterParticipants(
                    participants, RoomParticipant.ParticipantStatus.Accepted);

            log.info("매칭방 분류 결과 - Pending: {}, Accepted: {}", pending.size(), accepted.size());

            // 호스트 여부 확인
            boolean isHost = room.getMember().getMid().equals(loginMember.getMid());
            log.info("로그인 사용자가 호스트인지 확인 - roomId: {}, isHost: {}", roomId, isHost);

            // 대기중인 참가자 매핑
            Map<Member, List<Pet>> pendingMap = matchingService.getPendingMemberPets(room);
            log.info("대기중인 참가자 수: {}", pendingMap.size());
            model.addAttribute("pendingMap", pendingMap);

            // 승인된 참가자 매핑
            Map<Member, List<Pet>> acceptedMap = matchingService.getAcceptedMemberPets(room);
            if (acceptedMap == null) {
                acceptedMap = new HashMap<>(); // null 방지
            }
            log.info("승인된 참가자 수: {}", acceptedMap.size());
            model.addAttribute("acceptedMap", acceptedMap);

            // 모델에 매칭방 정보 및 참가자 정보 추가
            model.addAttribute("room", matchingService.convertToDto(room));
            model.addAttribute("pendingParticipants", pending);
            model.addAttribute("acceptedParticipants", accepted);
            model.addAttribute("isHost", isHost);

            // 호스트가 아닌 경우만 userPets 준비
            if (!isHost) {
                model.addAttribute("userPets", memberPets);

                // 사용자가 이미 신청했는지 여부 확인
                boolean hasApplied = matchingService.hasMemberApplied(room, loginMember);
                model.addAttribute("hasApplied", hasApplied);
                log.info("사용자가 이미 신청했는지 여부 - roomId: {}, hasApplied: {}", roomId, hasApplied);
            }

            log.info("매칭방 상세보기 데이터 준비 완료 - roomId: {}", roomId);
            return "matching/detail";
        } catch (ResourceNotFoundException e) {
            log.error("매칭방 상세 조회 실패 - roomId: {}, 오류: {}", roomId, e.getMessage());
            throw new ResourceNotFoundException(e.getMessage(), e);
        }
    }


    /**
     * 매칭방 수정 폼 페이지
     *
     * @param roomId             매칭방 ID
     * @param model              모델 객체
     * @param userDetails        인증된 사용자 정보
     * @param redirectAttributes 리다이렉트 시 속성
     * @return 매칭방 수정 폼 페이지 뷰 이름
     * @throws GeneralException       일반 예외 발생 시
     * @throws AccessDeniedException 접근 권한이 없을 때
     */
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable("id") Long roomId,
                           Model model,
                           @AuthenticationPrincipal UserDetails userDetails,
                           RedirectAttributes redirectAttributes) throws GeneralException, AccessDeniedException {
        // 로그인된 사용자 정보 확인
        if (userDetails == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "로그인이 필요합니다.");
            return "redirect:/member/login";
        }

        // 현재 로그인한 사용자의 정보를 가져와 멤버 조회
        Member loginMember = memberRepository.findByMid(userDetails.getUsername())
                .orElseThrow(() -> new GeneralException("사용자 정보를 찾을 수 없습니다."));

        try {
            // 매칭방 정보 조회
            MatchingRoom room = matchingService.getRoomById(roomId);
            if (!room.getMember().getMid().equals(loginMember.getMid())) {
                throw new AccessDeniedException("방장만 수정할 수 있습니다."); // 호스트가 아니면 접근 금지
            }

            // 매칭방 정보를 DTO로 변환
            MatchingRoomDTO dto = matchingService.convertToDto(room);
            dto.setRoomId(roomId);

            // 사용자 펫 목록을 가져와 모델에 추가
            List<Pet> memberPets = petService.findAllBymId(loginMember.getMid());
            model.addAttribute("memberPets", memberPets);

            // 매칭방 DTO 추가
            model.addAttribute("matchingRoomDTO", dto);

            // 로그인된 사용자 정보 추가
            model.addAttribute("user", userDetails);

            return "matching/edit";
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException(e.getMessage(), e);
        } catch (AccessDeniedException e) {
            throw new AccessDeniedException(e.getMessage());
        }
    }

    /**
     * 매칭방 수정 처리 메서드
     *
     * @param roomId             매칭방 ID
     * @param dto                매칭방 DTO
     * @param bindingResult      바인딩 결과
     * @param imageFile          업로드된 이미지 파일 (선택적)
     * @param userDetails        인증된 사용자 정보
     * @param model              모델 객체
     * @param redirectAttributes 리다이렉트 시 속성
     * @return 매칭방 수정 결과에 따른 리다이렉트 URL
     * @throws GeneralException 일반 예외 발생 시
     */
    @PostMapping("/edit/{id}")
    public String editSubmit(@PathVariable("id") Long roomId,
                             @Valid @ModelAttribute("matchingRoomDTO") MatchingRoomDTO dto,
                             BindingResult bindingResult,
                             @RequestParam(name = "imageFile", required = false) MultipartFile imageFile,
                             @AuthenticationPrincipal UserDetails userDetails,
                             Model model,
                             RedirectAttributes redirectAttributes) throws GeneralException {
        // 로그인된 사용자 정보 확인
        if (userDetails == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "로그인이 필요합니다.");
            return "redirect:/member/login";
        }

        // 현재 로그인한 사용자의 정보를 가져와 멤버 조회
        Member loginMember = memberRepository.findByMid(userDetails.getUsername())
                .orElseThrow(() -> new GeneralException("사용자 정보를 찾을 수 없습니다."));

        // 사용자 펫 목록을 가져와 모델에 추가
        List<Pet> memberPets = petService.findAllBymId(loginMember.getMid());
        model.addAttribute("memberPets", memberPets);

        // 로그인된 사용자 정보 추가
        model.addAttribute("user", userDetails);

        // 유효성 검사 오류가 있는 경우
        if (bindingResult.hasErrors()) {
            return "matching/edit";
        }

        try {
            // 이미지 파일 업로드 처리
            if (imageFile != null && !imageFile.isEmpty()) {
                String originalFilename = imageFile.getOriginalFilename();
                String uniqueFileName = UUID.randomUUID().toString() + "_" + originalFilename;
                String uploadDir = "C:/upload/"; // 실제 서버 환경에 맞게 수정
                File uploadDirectory = new File(uploadDir);
                if (!uploadDirectory.exists()) {
                    uploadDirectory.mkdirs(); // 디렉토리 생성
                }

                File destinationFile = new File(uploadDirectory, uniqueFileName);
                imageFile.transferTo(destinationFile); // 파일 저장

                String savedProfilePicture = "/upload/" + uniqueFileName;
                dto.setProfilePicture(savedProfilePicture); // 저장된 이미지 URL 설정
            }

            // 매칭방 정보 업데이트 서비스 호출
            matchingService.updateRoom(roomId, dto, loginMember);

            redirectAttributes.addFlashAttribute("successMessage", "매칭방이 성공적으로 수정되었습니다.");
            return "redirect:/matching/detail/" + roomId;

        } catch (Exception e) {
            throw new GeneralException("매칭방 수정 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 매칭방 삭제 처리 메서드
     *
     * @param id                 매칭방 ID
     * @param userDetails        인증된 사용자 정보
     * @param redirectAttributes 리다이렉트 시 속성
     * @return 매칭방 삭제 결과에 따른 리다이렉트 URL
     * @throws GeneralException       일반 예외 발생 시
     * @throws AccessDeniedException 접근 권한이 없을 때
     */
    @PostMapping("/delete/{id}")
    public String deleteRoom(@PathVariable Long id,
                             @AuthenticationPrincipal UserDetails userDetails,
                             RedirectAttributes redirectAttributes) throws GeneralException, AccessDeniedException {
        // 로그인된 사용자 정보 확인
        if (userDetails == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "로그인이 필요합니다.");
            return "redirect:/member/login";
        }

        // 현재 로그인한 사용자의 정보를 가져와 멤버 조회
        Member loginMember = memberRepository.findByMid(userDetails.getUsername())
                .orElseThrow(() -> new GeneralException("사용자 정보를 찾을 수 없습니다."));

        try {
            // 매칭방 정보 조회
            MatchingRoom room = matchingService.getRoomById(id);
            if (!room.getMember().getMid().equals(loginMember.getMid())) {
                throw new AccessDeniedException("방장만 삭제할 수 있습니다."); // 호스트가 아니면 접근 금지
            }

            // 매칭방 삭제 서비스 호출
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

    /**
     * 스케줄을 확정하는 메서드
     *
     * @param id                 매칭방 ID
     * @param userDetails        인증된 사용자 정보
     * @param redirectAttributes 리다이렉트 시 속성
     * @return 스케줄 확정 결과에 따른 리다이렉트 URL
     * @throws GeneralException 일반 예외 발생 시
     */
    @PostMapping("/confirm/{id}")
    public String confirmSchedule(@PathVariable Long id,
                                  @AuthenticationPrincipal UserDetails userDetails,
                                  RedirectAttributes redirectAttributes) throws GeneralException {
        // 로그인된 사용자 정보 확인
        if (userDetails == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "로그인이 필요합니다.");
            return "redirect:/member/login";
        }

        // 현재 로그인한 사용자의 정보를 가져와 멤버 조회
        Member loginMember = memberRepository.findByMid(userDetails.getUsername())
                .orElseThrow(() -> new GeneralException("사용자 정보를 찾을 수 없습니다."));

        log.info("Confirm request received for roomId: {}", id);

        try {
            // 매칭방 정보 조회
            MatchingRoom room = matchingService.getRoomById(id);
            // 승인된 참가자 목록 조회
            List<Member> participants = matchingService.getAcceptedParticipantsByRoomId(id);

            // 캘린더 서비스에 스케줄 저장 요청
            calendarService.saveSchedule(loginMember, room, participants);
            redirectAttributes.addFlashAttribute("successMessage", "스케줄이 확정되었습니다.");

            return "redirect:/matching/list";
        } catch (Exception ex) {
            throw new GeneralException("스케줄 확정 중 문제가 발생했습니다.", ex);
        }
    }

    /**
     * 매칭방에 참가 신청을 하는 메서드
     *
     * @param roomId             매칭방 ID
     * @param additionalPetIds   추가 참가할 펫 ID 리스트 (선택적)
     * @param userDetails        인증된 사용자 정보
     * @param redirectAttributes 리다이렉트 시 속성
     * @return 참가 신청 결과에 따른 리다이렉트 URL
     * @throws GeneralException 일반 예외 발생 시
     */
    @PostMapping("/apply/{roomId}")
    public String applyRoom(@PathVariable("roomId") Long roomId,
                            @RequestParam(value = "additionalPetIds", required = false) List<Long> additionalPetIds,
                            @AuthenticationPrincipal UserDetails userDetails,
                            RedirectAttributes redirectAttributes) throws GeneralException {
        // 로그인된 사용자 정보 확인
        if (userDetails == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "로그인이 필요합니다.");
            return "redirect:/member/login";
        }

        // 현재 로그인한 사용자의 정보를 가져와 멤버 조회
        Member loginMember = memberRepository.findByMid(userDetails.getUsername())
                .orElseThrow(() -> new GeneralException("사용자 정보를 찾을 수 없습니다."));

        try {
            log.info("Member {} is applying to room {}", loginMember.getMid(), roomId);
            log.info("Selected pet IDs: {}", additionalPetIds);
            // 참가 신청 서비스 호출
            matchingService.applyRoom(roomId, loginMember.getMid(), additionalPetIds);
            redirectAttributes.addFlashAttribute("successMessage", "참가 신청이 성공적으로 완료되었습니다.");
            return "redirect:/matching/detail/" + roomId;
        } catch (RuntimeException e) {
            throw new GeneralException("참가 신청 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 참가자를 승인하는 메서드
     *
     * @param roomId        매칭방 ID
     * @param memberId      승인할 회원 ID
     * @param userDetails   인증된 사용자 정보
     * @param redirectAttributes 리다이렉트 시 속성
     * @return 참가자 승인 결과에 따른 리다이렉트 URL
     * @throws GeneralException 일반 예외 발생 시
     */
    @PostMapping("/accept/{roomId}/{memberId}")
    public String acceptParticipant(@PathVariable("roomId") Long roomId,
                                    @PathVariable("memberId") String memberId,
                                    @AuthenticationPrincipal UserDetails userDetails,
                                    RedirectAttributes redirectAttributes) throws GeneralException {
        // 로그인된 사용자 정보 확인
        if (userDetails == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "로그인이 필요합니다.");
            return "redirect:/member/login";
        }

        // 현재 로그인한 사용자의 정보를 가져와 멤버 조회
        Member loginMember = memberRepository.findByMid(userDetails.getUsername())
                .orElseThrow(() -> new GeneralException("사용자 정보를 찾을 수 없습니다."));

        try {
            // 참가자 승인 서비스 호출
            matchingService.acceptParticipant(roomId, memberId);
            redirectAttributes.addFlashAttribute("successMessage", "참가자가 승인되었습니다.");
            return "redirect:/matching/detail/" + roomId;
        } catch (RuntimeException e) {
            throw new GeneralException("참가자 승인 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 참가자를 거절하는 메서드
     *
     * @param roomId        매칭방 ID
     * @param memberId      거절할 회원 ID
     * @param userDetails   인증된 사용자 정보
     * @param redirectAttributes 리다이렉트 시 속성
     * @return 참가자 거절 결과에 따른 리다이렉트 URL
     * @throws GeneralException 일반 예외 발생 시
     */
    @PostMapping("/reject/{roomId}/{memberId}")
    public String rejectParticipant(@PathVariable("roomId") Long roomId,
                                    @PathVariable("memberId") String memberId,
                                    @AuthenticationPrincipal UserDetails userDetails,
                                    RedirectAttributes redirectAttributes) throws GeneralException {
        // 로그인된 사용자 정보 확인
        if (userDetails == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "로그인이 필요합니다.");
            return "redirect:/member/login";
        }

        // 현재 로그인한 사용자의 정보를 가져와 멤버 조회
        Member loginMember = memberRepository.findByMid(userDetails.getUsername())
                .orElseThrow(() -> new GeneralException("사용자 정보를 찾을 수 없습니다."));

        try {
            // 참가자 거절 서비스 호출
            matchingService.rejectParticipant(roomId, memberId);
            redirectAttributes.addFlashAttribute("successMessage", "참가자가 거절되었습니다.");
            return "redirect:/matching/detail/" + roomId;
        } catch (RuntimeException e) {
            throw new GeneralException("참가자 거절 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 인증된 사용자 조회 메소드
     *
     * @param userDetails         인증된 사용자 정보
     * @param redirectAttributes  리다이렉트 시 속성
     * @param redirectUrl         리다이렉트 URL
     * @return 인증된 회원 객체
     * @throws GeneralException 일반 예외 발생 시
     */
    private Member getAuthenticatedMember(UserDetails userDetails, RedirectAttributes redirectAttributes, String redirectUrl) throws GeneralException {
        if (userDetails == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "로그인이 필요합니다.");
            throw new GeneralException("로그인이 필요합니다.");
        }

        return memberRepository.findByMid(userDetails.getUsername())
                .orElseThrow(() -> new GeneralException("사용자 정보를 찾을 수 없습니다."));
    }
}
