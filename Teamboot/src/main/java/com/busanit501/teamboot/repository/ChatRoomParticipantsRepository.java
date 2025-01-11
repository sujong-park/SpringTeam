package com.busanit501.teamboot.repository;

import com.busanit501.bootproject.domain.ChatRoomParticipants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface ChatRoomParticipantsRepository extends JpaRepository<ChatRoomParticipants, Long> {
    //유저를 채팅방에서 삭제
    @Transactional
    @Modifying
    @Query(value = "DELETE rp " +
            "FROM chat_room_participants rp " +
            "WHERE rp.chat_room_id = :roomId " +
            "AND rp.sender_id = :userId",
            nativeQuery = true)
    void deleteByRoomIdAndUserId(long roomId, String userId);
}
