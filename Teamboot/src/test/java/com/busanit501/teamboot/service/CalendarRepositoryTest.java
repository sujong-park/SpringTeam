package com.busanit501.teamboot.service;


import com.busanit501.teamboot.domain.Calendar;
import com.busanit501.teamboot.domain.MatchingRoom;
import com.busanit501.teamboot.domain.ScheduleStatus;
import com.busanit501.teamboot.domain.Member;
import com.busanit501.teamboot.repository.CalendarRepository;
import com.busanit501.teamboot.repository.MatchingRoomRepository;
import com.busanit501.teamboot.repository.MemberRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


//@DataJpaTest
//@Transactional
@Log4j2
@SpringBootTest
public class CalendarRepositoryTest {

    @Autowired
    private CalendarRepository calendarRepository;

    @Autowired
    private MatchingRoomRepository matchingRoomRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    public void testAddCalendarEvent() {

        Member testUser = memberRepository.findByMid("test2@gmail.com").orElseThrow(() -> new RuntimeException("user 오류"));
        MatchingRoom matching = matchingRoomRepository.findById(23L).orElseThrow(() -> new RuntimeException("matchingroom 오류"));

        // 일정 추가
        Calendar calendar = calendarRepository.save(
                Calendar.builder()
                        .member(testUser)
                        .schedulename(matching.getTitle())
                        .walkDate(matching.getMeetingDate())
                        .walkTime(matching.getMeetingTime())
                        .walkPlace(matching.getPlace())
                        .status(ScheduleStatus.SCHEDULED)
                        .matching(true)
                        .build()
        );

        // 일정 저장
        Calendar savedCalendar = calendarRepository.save(calendar);

    }


//    @Test
//    public void test() {
//        CalendarDTO calendarDTO = new CalendarDTO();
//    log.info(CalendarDTO.get);
//    }

}
