package com.busanit501.teamboot.repository;

import com.busanit501.teamboot.domain.ChatingRoom;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Log4j2
public class ChatingRoomRepositoryTest {
    @Autowired
    private ChatingRoomRepository chatingRoomRepository;

    @Test
    @Transactional
    public void searchAllMatchingRoomTest() {
        List<ChatingRoom> matchingRooms = chatingRoomRepository.searchAllChatingRoom("","test");
        // 리스트에서 하나씩 들고와서 출력
        matchingRooms.forEach(room -> log.info("채팅방: " + room));
    }
}
