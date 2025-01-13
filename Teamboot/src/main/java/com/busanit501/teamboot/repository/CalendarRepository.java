package com.busanit501.teamboot.repository;

import com.busanit501.teamboot.domain.Calendar;
import com.busanit501.teamboot.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CalendarRepository extends JpaRepository<Calendar, Long> {
    // 사용자와 일정 이름으로 일정 조회
    Optional<Calendar> findByMemberAndSchedulename(Member member, String scheduleName);


    @Query("SELECT c FROM Calendar c WHERE c.member.mid = :mid")
    List<Calendar> findBymid(String mid);

    @Query("SELECT c FROM Calendar c WHERE c.member.email = :email")
    List<Calendar> findBymail(String email);


    // 중복 데이터 조회 (email 기준)
    @Query("SELECT c.member.mid FROM Calendar c GROUP BY c.member.mid HAVING COUNT(c.member.mid)>1")
    List<String> findDuplicateMembers();

    // 특정 ID 목록의 데이터를 삭제
    @Modifying
    @Query("DELETE FROM Calendar c WHERE c.id IN :ids")
    void deleteMembersByIds(@Param("ids") List<Long> ids);

//    List<Calendar> findByMember_Mid(String mid);
}


