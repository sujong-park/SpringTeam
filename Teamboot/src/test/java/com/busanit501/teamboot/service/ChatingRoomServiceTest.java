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

import java.util.Collections;
import java.util.List;

@SpringBootTest
@Log4j2
public class ChatingRoomServiceTest {
    @Autowired
    private ChatingRoomService chatingRoomService;

    // 매칭방 추가 테스트
//    @Test
//    @Transactional
//    @Commit
//    public void testAddMatchingRoom() {
//        ChatingRoomDTO matchingRoomDTO = ChatingRoomDTO.builder()
//                .hostId("test") // 실제 존재하는 hostId로 테스트
//                .title("테스트 매칭방 제목")
//                .description("테스트 매칭방 설명")
//                .maxParticipants(5)
//                .currentParticipants(1)
//                .status(ChatingRoom.Status.Open)
//                .build();
//
//        ChatRoomParticipantsDTO roomParticipantsDTO = ChatRoomParticipantsDTO.builder()
//                .senderId("test") // hostId로 설정
//                .build();
//        log.info("새로운 매칭방 ID: " + matchingRoomDTO);
//        long roomId = chatingRoomService.addChatingRoom(matchingRoomDTO, roomParticipantsDTO);
//        log.info("새로운 매칭방 ID: " + roomId);
//    }

    // 매칭방 추가 테스트
    @Test
    @Transactional
    @Commit
    public void testAddChatingRoom() {
        // Given: 테스트용 데이터 생성
        String hostId = "test5";  // 실제 존재하는 hostId로 설정
        String title = "테스트방3";
        String description = "단체 초대 진행중";
        int maxParticipants = 5;
        int currentParticipants = 1;

        // 방 정보 DTO 설정
        ChatingRoomDTO chatingRoomDTO = ChatingRoomDTO.builder()
                .hostId(hostId)
                .title(title)
                .description(description)
                .maxParticipants(maxParticipants)
                .currentParticipants(currentParticipants)
                .status(ChatingRoom.Status.Open)
                .build();

        // 참여자 2명 정보 DTO 설정
        List<ChatRoomParticipantsDTO> roomParticipantsDTOList = List.of(
                ChatRoomParticipantsDTO.builder().senderId(hostId).build(),  // 호스트
                ChatRoomParticipantsDTO.builder().senderId("test2").build()  // 두 번째 참여자
        );

        // When: 매칭방 추가 메서드 실행
        long roomId = chatingRoomService.addChatingRoom(chatingRoomDTO, roomParticipantsDTOList);

        // Then: 생성된 매칭방 ID가 정상적으로 반환되는지 확인
        System.out.println("새로운 매칭방 ID: " + roomId);
    }

}
