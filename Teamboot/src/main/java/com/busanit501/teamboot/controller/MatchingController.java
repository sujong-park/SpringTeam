package com.busanit501.teamboot.controller;

import com.busanit501.teamboot.domain.MatchingRoom;
import com.busanit501.teamboot.domain.Pet;
import com.busanit501.teamboot.domain.RoomParticipant;
import com.busanit501.teamboot.domain.Member;
import com.busanit501.teamboot.dto.MatchingRoomDTO;
import com.busanit501.teamboot.exception.ResourceNotFoundException;
import com.busanit501.teamboot.repository.MemberRepository;
import com.busanit501.teamboot.service.MatchingService;
import com.busanit501.teamboot.service.PetService;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Log4j2
@Controller
@RequestMapping("/matching")
public class MatchingController {

    private final MatchingService matchingService;
    private final PetService petService;
    private final MemberRepository memberRepository;

    @Autowired
    public MatchingController(MatchingService matchingService, PetService petService, MemberRepository memberRepository) {
        this.matchingService = matchingService;
        this.petService = petService;
        this.memberRepository = memberRepository;
    }

    @GetMapping("/list")
    public String list(@AuthenticationPrincipal UserDetails user, MatchingRoomDTO matchingRoomDTO, Model model) {
        if (user == null) {
            return "redirect:/user/login";
        }
        List<MatchingRoomDTO> rooms = matchingService.getAllRooms();
        model.addAttribute("user", user);
        model.addAttribute("rooms", rooms);
        return "matching/list";
    }

    @GetMapping("/create")
    public String createForm(@AuthenticationPrincipal UserDetails user, Model model) {
        if (user == null) {
            return "redirect:/user/login";
        }
        Member loginMember = memberRepository.findByEmail(user.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("회원 정보를 찾을 수 없습니다."));

        List<Pet> userPets = petService.findAllByMemberId(loginMember.getMid());
        model.addAttribute("userPets", userPets);

        MatchingRoomDTO dto = new MatchingRoomDTO();
        dto.setMeetingDate(LocalDate.now());
        dto.setMeetingTime(LocalTime.now().withSecond(0).withNano(0));
        dto.setMaxParticipants(4L);
        model.addAttribute("matchingRoomDTO", dto);

        return "matching/create";
    }

    @PostMapping("/create")
    public String createSubmit(@AuthenticationPrincipal UserDetails user,
                               @Valid @ModelAttribute("matchingRoomDTO") MatchingRoomDTO dto,
                               BindingResult bindingResult,
                               @RequestParam(name = "imageFile", required = false) MultipartFile imageFile,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (user == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "로그인이 필요합니다.");
            return "redirect:/user/login";
        }
        Member loginMember = memberRepository.findByEmail(user.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("회원 정보를 찾을 수 없습니다."));

        if (bindingResult.hasErrors()) {
            List<Pet> userPets = petService.findAllByMemberId(loginMember.getMid());
            model.addAttribute("userPets", userPets);
            return "matching/create";
        }

        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                String originalFilename = imageFile.getOriginalFilename();
                String savedImageUrl = "/upload/" + originalFilename;
                dto.setImageUrl(savedImageUrl);
            }

            matchingService.createRoom(dto, loginMember);
            redirectAttributes.addFlashAttribute("successMessage", "매칭방이 성공적으로 생성되었습니다.");
            return "redirect:/matching/list";
        } catch (RuntimeException e) {
            List<Pet> userPets = petService.findAllByMemberId(loginMember.getMid());
            model.addAttribute("userPets", userPets);
            model.addAttribute("errorMessage", e.getMessage());
            return "matching/create";
        }
    }

    @GetMapping("/detail/{id}")
    public String getMatchingRoomDetail(@AuthenticationPrincipal UserDetails user,
                                        @PathVariable("id") Long roomId,
                                        Model model,
                                        RedirectAttributes redirectAttributes) {
        if (user == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "로그인이 필요합니다.");
            return "redirect:/user/login";
        }

        Member loginMember = memberRepository.findByEmail(user.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("회원 정보를 찾을 수 없습니다."));

        try {
            MatchingRoom room = matchingService.getRoomById(roomId);
            List<RoomParticipant> participants = matchingService.getParticipantsByRoomId(roomId);

            List<RoomParticipant> pending = matchingService.filterParticipants(participants, RoomParticipant.ParticipantStatus.Pending);
            List<RoomParticipant> accepted = matchingService.filterParticipants(participants, RoomParticipant.ParticipantStatus.Accepted);

            boolean isHost = room.getMhost().getMid().equals(loginMember.getMid());

            Map<Member, List<Pet>> pendingMap = matchingService.getPendingMemberPets(room);
            model.addAttribute("pendingMap", pendingMap);

            Map<Member, List<Pet>> acceptedMap = matchingService.getAcceptedMemberPets(room);
            model.addAttribute("acceptedMap", acceptedMap);

            model.addAttribute("room", room);
            model.addAttribute("pendingParticipants", pending);
            model.addAttribute("acceptedParticipants", accepted);
            model.addAttribute("isHost", isHost);

            if (!isHost) {
                List<Pet> userPets = petService.findAllByMemberId(loginMember.getMid());
                model.addAttribute("userPets", userPets);
            }

            return "matching/detail";
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/matching/list";
        }
    }

    @GetMapping("/edit/{id}")
    public String editForm(@AuthenticationPrincipal UserDetails user,
                           @PathVariable("id") Long roomId,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        if (user == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "로그인이 필요합니다.");
            return "redirect:/user/login";
        }

        Member loginMember = memberRepository.findByEmail(user.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("회원 정보를 찾을 수 없습니다."));

        try {
            MatchingRoom room = matchingService.getRoomById(roomId);

            if (!room.getMhost().getMid().equals(loginMember.getMid())) {
                redirectAttributes.addFlashAttribute("errorMessage", "방장만 수정할 수 있습니다.");
                return "redirect:/matching/detail/" + roomId;
            }

            MatchingRoomDTO dto = matchingService.convertToDto(room);
            dto.setRoomId(roomId);

            List<Pet> userPets = petService.findAllByMemberId(loginMember.getMid());
            model.addAttribute("matchingRoomDTO", dto);
            model.addAttribute("userPets", userPets);

            return "matching/edit";
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/matching/list";
        }
    }

    @PostMapping("/edit/{id}")
    public String editSubmit(@AuthenticationPrincipal UserDetails user,
                             @PathVariable("id") Long roomId,
                             @Valid @ModelAttribute("matchingRoomDTO") MatchingRoomDTO dto,
                             BindingResult bindingResult,
                             @RequestParam(name = "imageFile", required = false) MultipartFile imageFile,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (user == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "로그인이 필요합니다.");
            return "redirect:/user/login";
        }

        Member loginMember = memberRepository.findByEmail(user.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("회원 정보를 찾을 수 없습니다."));

        if (bindingResult.hasErrors()) {
            List<Pet> userPets = petService.findAllByMemberId(loginMember.getMid());
            model.addAttribute("userPets", userPets);
            return "matching/edit";
        }

        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                String originalFilename = imageFile.getOriginalFilename();
                String savedImageUrl = "/upload/" + originalFilename;
                dto.setImageUrl(savedImageUrl);
            }

            matchingService.updateRoom(roomId, dto, loginMember);
            redirectAttributes.addFlashAttribute("successMessage", "매칭방이 성공적으로 수정되었습니다.");
            return "redirect:/matching/detail/" + roomId;
        } catch (RuntimeException e) {
            List<Pet> userPets = petService.findAllByMemberId(loginMember.getMid());
            model.addAttribute("userPets", userPets);
            model.addAttribute("errorMessage", e.getMessage());
            return "matching/edit";
        }
    }
}
