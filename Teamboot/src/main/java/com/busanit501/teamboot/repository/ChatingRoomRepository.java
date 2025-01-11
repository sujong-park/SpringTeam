package com.busanit501.teamboot.repository;

import com.busanit501.teamboot.domain.ChatingRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatingRoomRepository extends JpaRepository<ChatingRoom, Integer> {
    //채팅방 조회(로그인한 유저 기준으로 조회)
    //최근 채팅 기준으로 정렬(채팅내역이 없다면 방생성시간을 기준으로 조회)
    @Query(value = "SELECT DISTINCT a.* " +
            "FROM chating_room a LEFT JOIN message b ON a.room_id = b.chat_room_id " +
            "LEFT JOIN chat_room_participants rp ON a.room_id = rp.chat_room_id " +
            "WHERE a.title like concat('%', :keyword, '%') " +
            "AND rp.sender_id = :userId " +
            "ORDER BY CASE " +
            "    WHEN b.reg_date IS NOT NULL THEN b.reg_date " +
            "    ELSE a.reg_date " +
            "END DESC",
            nativeQuery = true)
    List<ChatingRoom> searchAllChatingRoom(String keyword, String userId);


}
