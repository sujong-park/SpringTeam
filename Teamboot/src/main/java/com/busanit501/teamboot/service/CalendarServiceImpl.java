package com.busanit501.teamboot.service;


import com.busanit501.teamboot.domain.Calendar;
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

//
//    @Override
//    public void saveMatchingAndCalendar(MatchingRoomDTO matchingRoomDTO) {
//        MatchingRoom matchingRoom = matchinroomdtoEntity(matchingRoomDTO);
//        matchingRoomRepository.save(matchingRoom);
//
//        Calendar calendar = Calendar.builder()
//                .member(matchingRoom.getMember())
//                .schedulename(matchingRoom.getTitle())
//                .walkDate(matchingRoom.getMeetingDate())
//                .walkTime(matchingRoom.getMeetingTime())
//                .walkPlace(matchingRoom.getPlace())
//                .matching(true)  // matching된 일정이라는 뜻
//                .status(ScheduleStatus.SCHEDULED)
//                .build();
//
//        calendarRepository.save(calendar);
//        log.info("MatchingRoom과 Calendar 데이터 저장 완료");
//    }

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
    public void updateSchedule(Long id, CalendarDTO calendarDTO) {
        // 기존 일정 찾기
        Calendar existingCalendar = calendarRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("일정을 찾을 수 없습니다."));
        Calendar updatedCalendar = dtoToEntity(calendarDTO);
        existingCalendar = updatedCalendar;
        calendarRepository.save(existingCalendar);
    }

    @Override
    public void deleteSchedule(Long id) {
        calendarRepository.deleteById(id);
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
    public void saveSchedule(Member loginmember, com.busanit501.teamboot.domain.MatchingRoom room, List<Member> participants) {
        log.info("Saving schedule for roomId: {} with participants", room.getRoomId());

        try {
            // 매칭방 생성자(호스트) 저장
            com.busanit501.teamboot.domain.Calendar hostCalendar = com.busanit501.teamboot.domain.Calendar.builder()
                    .member(loginmember)
                    .schedulename(room.getTitle())
                    .walkDate(room.getMeetingDate())
                    .walkTime(room.getMeetingTime())
                    .walkPlace(room.getPlace())
                    .status(com.busanit501.teamboot.domain.ScheduleStatus.SCHEDULED)
                    .build();
            calendarRepository.save(hostCalendar);

            // 참여자 모두 저장
            for (Member participant : participants) {
                com.busanit501.teamboot.domain.Calendar participantCalendar = com.busanit501.teamboot.domain.Calendar.builder()
                        .member(participant)
                        .schedulename(room.getTitle())
                        .walkDate(room.getMeetingDate())
                        .walkTime(room.getMeetingTime())
                        .walkPlace(room.getPlace())
                        .status(com.busanit501.teamboot.domain.ScheduleStatus.SCHEDULED)
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
