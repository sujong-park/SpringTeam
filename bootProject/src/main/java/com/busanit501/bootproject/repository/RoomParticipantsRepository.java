package com.busanit501.bootproject.repository;

import com.busanit501.bootproject.domain.RoomParticipants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface RoomParticipantsRepository extends JpaRepository<RoomParticipants, Integer> {
    @Transactional
    @Modifying
    @Query(value = "DELETE " +
            "FROM room_participants rp " +
            "WHERE rp.chat_room_id = :roomId " +
            "AND rp.sender_id = :userId",
            nativeQuery = true) // nativeQuery 설정
    void deleteByRoomIdAndUserId(int roomId, int userId);
}
