package com.busanit501.teamboot.repository;

import com.busanit501.teamboot.domain.ChatingRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatingRoomRepository extends JpaRepository<ChatingRoom, Integer> {
    //매칭방 조회(로그인 회원이 포함된 매칭방)

    @Query(value = "SELECT DISTINCT a.* " +
            "FROM chating_room a LEFT JOIN message b ON a.room_id = b.chat_room_id " +
            "LEFT JOIN chat_room_participants rp ON a.room_id = rp.chat_room_id " +
            "WHERE a.title like concat('%', :keyword, '%') " +
            "AND rp.sender_id = :userId " +
            "ORDER BY b.sent_at DESC",
            nativeQuery = true)
    List<ChatingRoom> searchAllChatingRoom(String keyword, String userId);
}
