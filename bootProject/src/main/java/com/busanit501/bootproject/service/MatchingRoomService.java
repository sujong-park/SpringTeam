package com.busanit501.bootproject.service;

import com.busanit501.bootproject.dto.MatchingRoomDTO;
import com.busanit501.bootproject.dto.RoomParticipantsDTO;

import java.util.List;

public interface MatchingRoomService {
    //매칭룸생성()
    int addMatchingRoom(MatchingRoomDTO matchingRoomDTO, RoomParticipantsDTO roomParticipantsDTO);
    //매칭룸업데이트
    void updateMatchingRoom(MatchingRoomDTO matchingRoomDTO);
    void exitMatchingRoom(MatchingRoomDTO matchingRoomDTO);
    //매칭룸삭세
    void deleteMatchingRoom(int roomId);
    void deleteRoomParticipants(int roomId,int userId);
    //매칭룸전체조회
    List<MatchingRoomDTO> searchAllMatchingRoom(String keyword,int userId);
}
