package com.busanit501.teamboot.repository;

import com.busanit501.teamboot.domain.Calendar;
import com.busanit501.teamboot.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CalendarRepository extends JpaRepository<Calendar, Long> {
    // 사용자와 일정 이름으로 일정 조회
    Optional<Calendar> findByMemberAndSchedulename(Member member, String scheduleName);

}


