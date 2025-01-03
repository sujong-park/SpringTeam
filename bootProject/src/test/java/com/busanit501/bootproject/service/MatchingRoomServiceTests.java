package com.busanit501.bootproject.service;

import com.busanit501.bootproject.domain.RoomParticipantsStatus;
import com.busanit501.bootproject.domain.RoomStatus;
import com.busanit501.bootproject.dto.MatchingRoomDTO;
import com.busanit501.bootproject.dto.RoomParticipantsDTO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@SpringBootTest
@Log4j2
public class MatchingRoomServiceTests {
    @Autowired
    private MatchingRoomService matchingRoomService;

    // 매칭방 추가 테스트
    @Test
    public void testAddMatchingRoom() {
        MatchingRoomDTO matchingRoomDTO = MatchingRoomDTO.builder()
                .hostId(1) // 실제 존재하는 hostId로 테스트
                .title("테스트 매칭방 제목")
                .description("테스트 매칭방 설명")
                .maxParticipants(5)
                .currentParticipants(1)
                .status(RoomStatus.Open)
                .build();

        RoomParticipantsDTO roomParticipantsDTO = RoomParticipantsDTO.builder()
                .senderId(1) // hostId로 설정
                .status(RoomParticipantsStatus.Pending)
                .build();

        int roomId = matchingRoomService.addMatchingRoom(matchingRoomDTO, roomParticipantsDTO);
        log.info("새로운 매칭방 ID: " + roomId);
    }

    // 매칭방 업데이트 테스트
    @Test
    public void testUpdateMatchingRoom() {
        MatchingRoomDTO updateDTO = MatchingRoomDTO.builder()
                .roomId(16) // 업데이트할 매칭방의 ID
                .title("업데이트된 매칭방 제목")
                .description("업데이트된 매칭방 설명")
                .maxParticipants(10)
                .currentParticipants(3)
                .status(RoomStatus.Closed)
                .build();

        matchingRoomService.updateMatchingRoom(updateDTO);
        log.info("매칭방 업데이트 완료: " + updateDTO.getRoomId());
    }

    // 매칭방 삭제 테스트
    @Test
    public void testDeleteMatchingRoom() {
        int roomIdToDelete = 56; // 삭제할 매칭방 ID
        matchingRoomService.deleteMatchingRoom(roomIdToDelete);
        log.info("매칭방 삭제 완료: " + roomIdToDelete);
    }

    // 매칭방 검색 테스트
    @Test
    public void testSearchAllMatchingRooms() {
        String keyword = "매칭"; // 검색 키워드
        int userId = 1;
        var matchingRooms = matchingRoomService.searchAllMatchingRoom(keyword,userId);

        matchingRooms.forEach(room -> {
            log.info("Room ID: " + room.getRoomId());
            log.info("Host ID: " + room.getHostId());
            log.info("Title: " + room.getTitle());
            log.info("Description: " + room.getDescription());
            log.info("Status: " + room.getStatus());
            log.info("----------------------------");
        });
    }
}
