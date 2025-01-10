package com.busanit501.teamboot.repository;

import com.busanit501.teamboot.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;

import java.util.List;

public interface ChatMemberRepository extends JpaRepository<Member, String> {
    //채팅방에서 초대 할때, 조회하는 맴버 쿼리
    @Query(value = "SELECT m.* " +
            "FROM member m " +
            "WHERE m.mid NOT IN (SELECT rp.sender_id " +
            "FROM chat_room_participants rp " +
            "WHERE rp.chat_room_id = :roomId) " +
            "AND m.name like concat('%', :keyword, '%') "
            ,nativeQuery = true)
    List<Member> searchInviteUserList(String keyword, long roomId);

    //채팅방을 생성 할때, 조회하는 맴버 쿼리
    @Query(value = "SELECT m.* " +
            "FROM member m " +
            "WHERE m.mid != :userId " +
            "AND m.name like concat('%', :keyword, '%') "
            ,nativeQuery = true)
    List<Member> searchCreateUserList(String keyword, String userId);

    Optional<Member> findByMid(String mid);
}
