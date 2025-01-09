package com.busanit501.teamboot.service;

import com.busanit501.teamboot.domain.ChatingRoom;
import com.busanit501.teamboot.dto.ChatRoomParticipantsDTO;
import com.busanit501.teamboot.dto.ChatingRoomDTO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Log4j2
public class ChatingRoomServiceTest {
    @Autowired
    private ChatingRoomService chatingRoomService;

    // 매칭방 추가 테스트
    @Test
    @Transactional
    @Commit
    public void testAddMatchingRoom() {
        ChatingRoomDTO matchingRoomDTO = ChatingRoomDTO.builder()
                .hostId("test") // 실제 존재하는 hostId로 테스트
                .title("테스트 매칭방 제목")
                .description("테스트 매칭방 설명")
                .maxParticipants(5)
                .currentParticipants(1)
                .status(ChatingRoom.Status.Open)
                .build();

        ChatRoomParticipantsDTO roomParticipantsDTO = ChatRoomParticipantsDTO.builder()
                .senderId("test") // hostId로 설정
                .build();
        log.info("새로운 매칭방 ID: " + matchingRoomDTO);
        long roomId = chatingRoomService.addChatingRoom(matchingRoomDTO, roomParticipantsDTO);
        log.info("새로운 매칭방 ID: " + roomId);
    }
}
