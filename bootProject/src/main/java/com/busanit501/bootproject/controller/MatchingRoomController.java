package com.busanit501.bootproject.controller;

import com.busanit501.bootproject.dto.MatchingRoomDTO;
import com.busanit501.bootproject.dto.RoomRegisterDTO;
import com.busanit501.bootproject.dto.UserDTO;
import com.busanit501.bootproject.service.MatchingRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;


@Controller
@Log4j2
@RequestMapping("/matchingRoom")
@RequiredArgsConstructor
public class MatchingRoomController {
    private final MatchingRoomService matchingRoomService;
    // 매칭룸 리스트 조회
    @GetMapping("/roomList")
    public void roomList(@RequestParam(required = false, defaultValue = "") String keyword
            , UserDTO userDTO
            , Model model) {
        int userId = 1; // 현재 유저 ID (테스트용)
        List<MatchingRoomDTO> roomList = matchingRoomService.searchAllMatchingRoom(keyword, userId);
        model.addAttribute("roomList", roomList);
        model.addAttribute("keyword", keyword);
    }

    @PostMapping("/roomRegister")
    public String registerRoom(@RequestBody RoomRegisterDTO roomRegisterDTO, RedirectAttributes redirectAttributes) {
        matchingRoomService.addMatchingRoom(roomRegisterDTO.getMatchingRoomDTO(), roomRegisterDTO.getRoomParticipantsDTO());
        // 채팅방 목록을 다시 조회하여 모델에 추가
//        List<MatchingRoomDTO> roomList = matchingRoomService.searchAllMatchingRoom("", 1); // 1은 현재 유저 ID (테스트용)
//
//        // 모델에 방 목록과 메시지를 추가
//        redirectAttributes.addFlashAttribute("roomList", roomList);
//        redirectAttributes.addFlashAttribute("message", "채팅방이 성공적으로 생성되었습니다.");

        // 리다이렉트(작동안댐)
        return "redirect:/matchingRoom/roomList";
    }
    @PostMapping("/matchingRoom/roomUAD")
    public void roomUAD(@RequestBody RoomRegisterDTO roomRegisterDTO){
        matchingRoomService.exitMatchingRoom(roomRegisterDTO.getMatchingRoomDTO());
        matchingRoomService.deleteRoomParticipants(roomRegisterDTO.getMatchingRoomDTO().getRoomId(),
                roomRegisterDTO.getRoomParticipantsDTO().getSenderId());
    }

    @ResponseBody
    @PutMapping("/{roomId}")
    public Map<String, Integer> updateRoom(@RequestBody MatchingRoomDTO roomDTO,
                                           @PathVariable("roomId") int roomId) {
        matchingRoomService.updateMatchingRoom(roomDTO);
        Map<String, Integer> map = Map.of("roomId",roomId);
        return map;
    }

    // 채팅방 삭제
    @ResponseBody
    @DeleteMapping(value = "/{roomId}")
    public  Map<String, Integer> deleteRoom(@PathVariable("roomId") int roomId) {
        matchingRoomService.deleteMatchingRoom(roomId);
        Map<String, Integer> map = Map.of("roomId",roomId);
        log.info("map : " + map);
        return map;
//        return "redirect:/matchingRoom/roomList";
    }


}
