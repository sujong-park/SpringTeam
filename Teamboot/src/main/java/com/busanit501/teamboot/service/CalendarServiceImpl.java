package com.busanit501.teamboot.service;


import com.busanit501.teamboot.domain.Calendar;
import com.busanit501.teamboot.domain.MatchingRoom;
import com.busanit501.teamboot.domain.Member;
import com.busanit501.teamboot.domain.ScheduleStatus;
import com.busanit501.teamboot.dto.CalendarDTO;
import com.busanit501.teamboot.repository.CalendarRepository;
import com.busanit501.teamboot.repository.MatchingRoomRepository;
import com.busanit501.teamboot.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class CalendarServiceImpl implements CalendarService {

    private final CalendarRepository calendarRepository;
    private final MatchingRoomRepository matchingRoomRepository;
    private final MemberRepository memberRepository;

    @Override
    public List<CalendarDTO> getUserSchedules(String mid) {
        log.info("미드 값 확인: {}", mid);
        List<Calendar> calendars = calendarRepository.findBymid(mid);
        log.info("calendars에서 데이터가 넘어오나 ? 서비스"+calendars);
        return calendars.stream().map(this::entityToDto).collect(Collectors.toList());
    }


    @Override
    public Calendar addSchedule(CalendarDTO calendarDTO) {
        Calendar calendar = dtoToEntity(calendarDTO);

        try {
            Calendar save = calendarRepository.save(calendar);
            log.info("!");
        } catch (Exception e) {
            log.info("rkskek"+e.getMessage());
        }

        return calendar;
    }

    @Override
    @Scheduled(cron = "0 0 * * * *") // 매 시간마다 실행
    @Transactional
    public void updateScheduleStatus() {
        List<Calendar> schedules = calendarRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        schedules.forEach(schedule -> {
            LocalDateTime scheduleTime = LocalDateTime.of(schedule.getWalkDate(), schedule.getWalkTime());
            if (scheduleTime.isBefore(now)) {
                schedule.changeStatus(ScheduleStatus.COMPLETED);
            }
        });
        calendarRepository.saveAll(schedules);
        log.info("ScheduleStatus 업데이트 완료");
    }


    @Override
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
                    .matching(true)
                    .build();
            log.info("리무브전 중복확인"+hostCalendar);
            removeDuplicateMembers(hostCalendar);
            log.info("리무브 후 중복확인"+hostCalendar);
//            savecal.removeDuplicateMembers();

            // 참여자 모두 저장
            for (Member participant : participants) {
                Calendar participantCalendar = Calendar.builder()
                        .member(participant)
                        .schedulename(room.getTitle())
                        .walkDate(room.getMeetingDate())
                        .walkTime(room.getMeetingTime())
                        .walkPlace(room.getPlace())
                        .status(ScheduleStatus.SCHEDULED)
                        .matching(true)
                        .build();

                removeDuplicateMembers(participantCalendar);
//                removeDuplicateMembers();

                log.info("Schedule saved for participant: {}", participant.getMid());
            }



            log.info("Schedule saved successfully for roomId: {}", room.getRoomId());
        } catch (Exception ex) {
            log.error("Error while saving schedule: ", ex);
            throw ex;
        }
    }

    public void removeDuplicateMembers(Calendar newCalendar) {
// 해당 일정에서 이미 참여한 멤버인지 확인
        List<Calendar> existingCalendars = calendarRepository.selectcalendar(
                newCalendar.getSchedulename(),
                newCalendar.getMember()
        );
        log.info("중복된 멤버인지 확인"+existingCalendars);

// 이미 참여한 멤버가 없다면 새로 추가
        if (existingCalendars.isEmpty()) {
            calendarRepository.save(newCalendar);
        } else {
            log.info("Duplicate member not saved: {}", newCalendar.getMember().getMid());
        }
    }


}
