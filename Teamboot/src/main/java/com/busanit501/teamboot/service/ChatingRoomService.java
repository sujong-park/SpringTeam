package com.busanit501.teamboot.service;

import com.busanit501.teamboot.dto.ChatRoomParticipantsDTO;
import com.busanit501.teamboot.dto.ChatingRoomDTO;

import java.util.List;

public interface ChatingRoomService {
    //채팅방 생성(본인만 포함)
    //long addChatingRoom(ChatingRoomDTO matchingRoomDTO, ChatRoomParticipantsDTO roomParticipantsDTO);

    //채팅방 생성(여러명 초대가능)
    long addChatingRoom(ChatingRoomDTO chatingRoomDTO, List<ChatRoomParticipantsDTO> chatRoomParticipantsDTOList);

    //채팅방 수정(방장만 가능)
    void updateChatingRoom(ChatingRoomDTO matchingRoomDTO);

    //채팅방 나가기(방장을 제외한 유저만 가능)
    void exitChatingRoom(ChatingRoomDTO matchingRoomDTO);
    void deleteRoomParticipants(long roomId,String userId);

    //채팅방에 초대(모든 유저 가능)
    void inviteChatingRoom(ChatingRoomDTO matchingRoomDTO,ChatRoomParticipantsDTO roomParticipantsDTO);

    //채팅방 삭제(방장만 가능)
    void deleteChatingRoom(long roomId);

    //채팅방 조회(로그인한 유저가 포함한 채팅방 조회)
    List<ChatingRoomDTO> searchAllChatingRoom(String keyword, String userId);
}
