package com.busanit501.teamboot.service;

import com.busanit501.teamboot.dto.ChatRoomParticipantsDTO;
import com.busanit501.teamboot.dto.ChatingRoomDTO;

import java.util.List;

public interface ChatingRoomService {
    //매칭룸생성()
//    long addChatingRoom(ChatingRoomDTO matchingRoomDTO, ChatRoomParticipantsDTO roomParticipantsDTO);
    long addChatingRoom(ChatingRoomDTO chatingRoomDTO, List<ChatRoomParticipantsDTO> chatRoomParticipantsDTOList);
    //매칭룸업데이트
    void updateChatingRoom(ChatingRoomDTO matchingRoomDTO);
    void exitChatingRoom(ChatingRoomDTO matchingRoomDTO);
    void inviteChatingRoom(ChatingRoomDTO matchingRoomDTO,ChatRoomParticipantsDTO roomParticipantsDTO);
    //매칭룸삭세
    void deleteChatingRoom(long roomId);
    void deleteRoomParticipants(long roomId,String userId);
    //매칭룸전체조회
    List<ChatingRoomDTO> searchAllChatingRoom(String keyword, String userId);
}
