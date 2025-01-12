package com.busanit501.teamboot.service;

import com.busanit501.teamboot.domain.Calendar;
import com.busanit501.teamboot.domain.MatchingRoom;
import com.busanit501.teamboot.domain.Member;
import com.busanit501.teamboot.domain.ScheduleStatus;
import com.busanit501.teamboot.repository.CalendarRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Log4j2
public class CalendarService {

    @Autowired
    private CalendarRepository calendarRepository;

    @Transactional
    public void saveSchedule(Member loginmember, MatchingRoom room, List<Member> participants) {
        log.info("Saving schedule for roomId: {} with participants", room.getRoomId());

        try {
            // 매칭방 생성자(호스트) 저장
            Calendar hostCalendar = Calendar.builder()
                    .member(loginmember)
                    .schedulename(room.getTitle())
                    .walkDate(room.getMeetingDate())
                    .walkTime(room.getMeetingTime())
                    .walkPlace(room.getPlace())
                    .status(ScheduleStatus.SCHEDULED)
                    .build();
            calendarRepository.save(hostCalendar);

            // 참여자 모두 저장
            for (Member participant : participants) {
                Calendar participantCalendar = Calendar.builder()
                        .member(participant)
                        .schedulename(room.getTitle())
                        .walkDate(room.getMeetingDate())
                        .walkTime(room.getMeetingTime())
                        .walkPlace(room.getPlace())
                        .status(ScheduleStatus.SCHEDULED)
                        .build();
                calendarRepository.save(participantCalendar);
                log.info("Schedule saved for participant: {}", participant.getMid());
            }

            log.info("Schedule saved successfully for roomId: {}", room.getRoomId());
        } catch (Exception ex) {
            log.error("Error while saving schedule: ", ex);
            throw ex;
        }
    }
}


