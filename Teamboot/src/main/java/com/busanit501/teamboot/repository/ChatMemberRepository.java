package com.busanit501.teamboot.repository;

import com.busanit501.teamboot.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;

import java.util.List;

public interface ChatMemberRepository extends JpaRepository<Member, String> {
    @Query(value = "SELECT m.* " +
            "FROM member m " +
            "WHERE m.mid NOT IN (SELECT rp.sender_id " +
            "FROM chat_room_participants rp " +
            "WHERE rp.chat_room_id = :roomId) " +
            "AND m.name like concat('%', :keyword, '%') "
            ,nativeQuery = true)
    List<Member> searchInviteUserList(String keyword, long roomId);

    Optional<Member> findByMid(String mid);
}
