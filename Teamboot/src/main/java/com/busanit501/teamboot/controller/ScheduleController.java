package com.busanit501.teamboot.controller;

import com.busanit501.teamboot.domain.Calendar;
import com.busanit501.teamboot.dto.CalendarDTO;
import com.busanit501.teamboot.service.CalendarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping("/schedule")
@Log4j2
@RequiredArgsConstructor
public class ScheduleController {

    private final CalendarService calendarService;



    @GetMapping("/{mid}")
    public ResponseEntity<List<CalendarDTO>> getUserSchedules(@PathVariable("mid") String mid) {
        log.info("Received mid 값 확인: {}", mid);
        List<CalendarDTO> schedules = calendarService.getUserSchedules(mid);
        log.info("calendars에서 데이터가 넘어오나 ? 컨트롤러"+schedules);
        log.info("Schedule확인: {}", schedules);

        schedules.forEach(calendarDTO -> {

            LocalDateTime localDateTime = calendarDTO.getWalkDate().atTime(calendarDTO.getWalkTime());
            String isoDate = localDateTime.toString();  // LocalDateTime은 자동으로 ISO 형식으로 변환

            calendarDTO.setWalkDateIso(isoDate);
        });

        return ResponseEntity.ok(schedules);
    }


    @PostMapping("/add")
    public ResponseEntity<String> addSchedule(@RequestBody CalendarDTO calendarDTO) {
            try {
                // 서비스에서 일정 추가
                Calendar calendar = calendarService.addSchedule(calendarDTO);

                // 성공적으로 일정이 추가되었다는 응답
                return ResponseEntity.ok("일정이 저장 되었습니다");
            } catch (Exception e) {
                // 실패 시 에러 메시지 응답
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("일정 추가 실패!!!!");
            }


    }


}
