package com.busanit501.teamboot.repository;

import com.busanit501.teamboot.domain.Message;
import com.busanit501.teamboot.dto.MessageDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Integer> {
    //채팅방 대화 목록 조회
    @Query("SELECT new com.busanit501.teamboot.dto.MessageDTO(a.messageId, " +
            "a.chatRoom.roomId, " +
            "a.sender.mid, " +
            "a.content, " +
            "a.sentAt, " +
            "a.isRead, " +
            "u.name) " +
            "FROM Message a " +
            "JOIN a.sender u " +
            "WHERE a.chatRoom.roomId = :roomId " +
            "ORDER BY a.messageId ASC ")
    List<MessageDTO> searchMessageByMatchingRoomId(long roomId);

    //유저 채팅 삭제(로그인한 유저 기준)
    @Transactional
    @Modifying
    @Query(value = "DELETE m " +
            "FROM message m " +
            "WHERE m.chat_room_id = :roomId " +
            "AND m.sender_id = :userId " ,nativeQuery = true)
    void deleteAllMessagesByUserId(String userId,long roomId);
}
