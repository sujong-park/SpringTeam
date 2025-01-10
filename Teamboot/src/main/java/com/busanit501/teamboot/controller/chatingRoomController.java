package com.busanit501.teamboot.controller;

import com.busanit501.teamboot.dto.*;
import com.busanit501.teamboot.service.ChatMemberService;
import com.busanit501.teamboot.service.ChatingRoomService;
import com.busanit501.teamboot.service.MemberService;
import com.busanit501.teamboot.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@Log4j2
@RequestMapping("/chatingRoom")
@RequiredArgsConstructor
public class chatingRoomController {
    private final ChatingRoomService chatingRoomService;
    private final MessageService messageService;
    private final ChatMemberService chatMemberServiceme;
    // 채팅방 목록 조회
    @GetMapping("/roomList")
    public void roomList(@AuthenticationPrincipal UserDetails user
            ,@RequestParam(required = false, defaultValue = "") String keyword
            , Model model) {

        String userId = user.getUsername(); // 현재 유저 ID (테스트용)
        List<ChatingRoomDTO> roomList = chatingRoomService.searchAllChatingRoom(keyword, userId);
        model.addAttribute("roomList", roomList);
        model.addAttribute("keyword", keyword);
    }
    //채팅방 추가
//    @ResponseBody
//    @PostMapping("/roomRegister")
//    public void registerRoom(@RequestBody ChatRoomRegisterDTO chatRoomRegisterDTO) {
//        log.info("chatingRoomController chatRoomRegisterDTO: " + chatRoomRegisterDTO.getChatingRoomDTO());
//        chatingRoomService.addChatingRoom(chatRoomRegisterDTO.getChatingRoomDTO(),
//                chatRoomRegisterDTO.getChatRoomParticipantsDTO());
//    }
    @ResponseBody
    @PostMapping("/roomRegister")
    public void registerRoom(@RequestBody ChatRoomAllRegisterDTO chatRoomAllRegisterDTO) {
        log.info("chatRoomAllRegisterDTO: " + chatRoomAllRegisterDTO); // 전체 DTO 출력
        log.info("chatingRoomDTO: " + chatRoomAllRegisterDTO.getChatingRoomDTO());
        log.info("chatRoomParticipantsDTOList: " + chatRoomAllRegisterDTO.getChatRoomParticipantsDTO());
        chatingRoomService.addChatingRoom(
                chatRoomAllRegisterDTO.getChatingRoomDTO(),
                chatRoomAllRegisterDTO.getChatRoomParticipantsDTO()
        );
    }




    //매칭방 나가기
    @ResponseBody
    @PostMapping("/exit")
    public Map<String,String> roomUAD(@RequestBody ChatRoomRegisterDTO chatRoomRegisterDTO){
        log.info("RoomRegisterDTO: " + chatRoomRegisterDTO);
        chatingRoomService.exitChatingRoom(chatRoomRegisterDTO.getChatingRoomDTO());
        chatingRoomService.deleteRoomParticipants(chatRoomRegisterDTO.getChatRoomParticipantsDTO().getChatRoomId(),
                chatRoomRegisterDTO.getChatRoomParticipantsDTO().getSenderId());
        messageService.deleteAllMessagesByUser(chatRoomRegisterDTO.getChatRoomParticipantsDTO().getSenderId(),
                chatRoomRegisterDTO.getChatRoomParticipantsDTO().getChatRoomId());
        Map<String, String> map = Map.of("UserId",chatRoomRegisterDTO.getChatRoomParticipantsDTO().getSenderId());
        return map;
    }
    //채팅방 수정
    @ResponseBody
    @PutMapping("/{roomId}")
    public Map<String, Long> updateRoom(@RequestBody ChatingRoomDTO roomDTO,
                                        @PathVariable("roomId") long roomId) {
        chatingRoomService.updateChatingRoom(roomDTO);
        Map<String, Long> map = Map.of("roomId",roomId);
        return map;
    }
    // 채팅방 삭제
    @ResponseBody
    @DeleteMapping(value = "/{roomId}")
    public Map<String, Long> deleteRoom(@PathVariable("roomId") long roomId) {
        chatingRoomService.deleteChatingRoom(roomId);
        Map<String, Long> map = Map.of("roomId",roomId);
        //log.info("map : " + map);
        return map;
    }
    //채팅 조회
    @ResponseBody
    @GetMapping("/chatList/{roomId}")
    public List<MessageDTO> getChatList(@PathVariable("roomId") long roomId){
        List<MessageDTO> list = messageService.searchMessage(roomId);
        log.info("list : " + list);
        return list;
    }

    //메세지 작성
    @ResponseBody
    @PostMapping("/messageRegister")
    public Map<String, Long> registerMessage(@RequestBody MessageDTO messageDTO) {
        long messageId = messageService.addMessage(messageDTO);
        Map<String,Long> map = Map.of("messageId",messageId);
        log.info("map : " + map);
        return map;
    }
    //메세지 삭제(본인꺼)
    @ResponseBody
    @DeleteMapping(value = "/messageDelete/{messageId}")
    public Map<String, Long> deleteMessage(@PathVariable("messageId") long messageId) {
        messageService.deleteMessage(messageId);
        Map<String, Long> map = Map.of("messageId",messageId);
        //log.info("map : " + map);
        return map;
    }
    //유저 조회
    @ResponseBody
    @GetMapping("/userListCreate/{userId}")
    public List<MemberDTO> getUserListCreate(@RequestParam(required = false, defaultValue = "") String keyword,
                                       @PathVariable("userId") String userId){
        log.info("키워드3 : " + keyword);
        log.info("roomId : " + userId);
        List<MemberDTO> list = chatMemberServiceme.searchCreateUser(keyword, userId);
        log.info("초대가능한 유저 리스트 : " + list);
        return list;
    }
    //유저 조회
    @ResponseBody
    @GetMapping("/userListInvite/{roomId}")
    public List<MemberDTO> getUserList(@RequestParam(required = false, defaultValue = "") String keyword,
                                       @PathVariable("roomId") long roomId){
        log.info("키워드3 : " + keyword);
        log.info("roomId : " + roomId);
        List<MemberDTO> list = chatMemberServiceme.searchInviteUser(keyword, roomId);
        log.info("초대가능한 유저 리스트 : " + list);
        return list;
    }
    //채팅방에 유저 초대
    @ResponseBody
    @PostMapping("/invite")
    public List<Map<String, String>> inviteUsers(@RequestBody List<ChatRoomRegisterDTO> chatRoomRegisterDTOList) {
        List<Map<String, String>> responseList = new ArrayList<>();

        for (ChatRoomRegisterDTO chatRoomRegisterDTO : chatRoomRegisterDTOList) {
            log.info("Processing ChatRoomRegisterDTO: " + chatRoomRegisterDTO);

            // 초대 처리: 채팅방에 유저를 초대하는 서비스 호출
            chatingRoomService.inviteChatingRoom(
                    chatRoomRegisterDTO.getChatingRoomDTO(),
                    chatRoomRegisterDTO.getChatRoomParticipantsDTO()
            );

            // 처리 결과 저장
            Map<String, String> result = Map.of("UserId", chatRoomRegisterDTO.getChatRoomParticipantsDTO().getSenderId());
            responseList.add(result);
        }

        return responseList;
    }
//    @GetMapping("/list")
//    public void list(@AuthenticationPrincipal UserDetails user, Model model) {
//        model.addAttribute("user", user);
//    }
//
//    @GetMapping("/register")
//    public void register(@AuthenticationPrincipal UserDetails user, Model model) {
//        model.addAttribute("user", user);
//    }
//    @PostMapping("/register")
//    public String registerPost(@AuthenticationPrincipal UserDetails user, Model model) {
//        model.addAttribute("user", user);
//        return null;
//    }
//
//    @GetMapping("/read")
//    public void read(@AuthenticationPrincipal UserDetails user, Model model) {
//        model.addAttribute("user", user);
//    }
//
//    @GetMapping("/update")
//    public void update(@AuthenticationPrincipal UserDetails user, Model model) {
//        model.addAttribute("user", user);
//    }
//    @PostMapping("/update")
//    public String updatePost(@AuthenticationPrincipal UserDetails user, Model model) {
//        model.addAttribute("user", user);
//        return null;
//    }
//
//    @PostMapping("/delete")
//    public String delete(@AuthenticationPrincipal UserDetails user, Model model) {
//        model.addAttribute("user", user);
//        return null;
//    }

}
