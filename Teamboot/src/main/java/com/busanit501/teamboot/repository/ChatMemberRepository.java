package com.busanit501.teamboot.repository;

import com.busanit501.teamboot.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;

import java.util.List;

public interface ChatMemberRepository extends JpaRepository<Member, String> {
    //채팅방에 포함되어 있지 않은 유저 목록 조회
    @Query(value = "SELECT m.* " +
            "FROM member m " +
            "WHERE m.mid NOT IN (SELECT rp.sender_id " +
            "FROM chat_room_participants rp " +
            "WHERE rp.chat_room_id = :roomId) " +
            "AND m.name like concat('%', :keyword, '%') "
            ,nativeQuery = true)
    List<Member> searchInviteUserList(String keyword, long roomId);

    //로그인한 본인을 제외한 유저 목록 조회
    @Query(value = "SELECT m.* " +
            "FROM member m " +
            "WHERE m.mid != :userId " +
            "AND m.name like concat('%', :keyword, '%') "
            ,nativeQuery = true)
    List<Member> searchCreateUserList(String keyword, String userId);

    //Member엔티티에서 mid기준으로 데이터 조회
    Optional<Member> findByMid(String mid);
}
