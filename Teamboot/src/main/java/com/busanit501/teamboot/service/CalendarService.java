package com.busanit501.teamboot.service;

import com.busanit501.teamboot.domain.Calendar;
import com.busanit501.teamboot.domain.MatchingRoom;
import com.busanit501.teamboot.domain.Member;
import com.busanit501.teamboot.dto.CalendarDTO;

import java.util.List;
import java.util.stream.Collectors;

public interface CalendarService {


    void saveSchedule(Member loginmember, MatchingRoom room, List<Member> participants);

    //    void saveMatchingAndCalendar(MatchingRoomDTO matchingRoomDTO);
    List<CalendarDTO> getUserSchedules(String mid);

    void updateScheduleStatus();

    Calendar addSchedule(CalendarDTO calendarDTO);

//    void updateSchedule(Long id, CalendarDTO calendarDTO);

//    void deleteSchedule(Long id);


    default CalendarDTO entityToDto(Calendar calendar) {
        return CalendarDTO.builder()
                .scheduleId(calendar.getScheduleId())
                .mid(calendar.getMember().getMid())
                .schedulename(calendar.getSchedulename())
                .walkDate(calendar.getWalkDate())
                .walkTime(calendar.getWalkTime())
                .walkPlace(calendar.getWalkPlace())
                .status(calendar.getStatus())
                .matching(calendar.getMatching())
                .schedulStart(calendar.getSchedulStart())
                .schedulEnd(calendar.getSchedulEnd())
                .build();
    }

    // DTO to Entity
    default Calendar dtoToEntity(CalendarDTO dto) {
        return Calendar.builder()
                .member(Member.builder().mid(dto.getMid()).build())
                .schedulename(dto.getSchedulename())
                .walkDate(dto.getWalkDate())
                .walkTime(dto.getWalkTime())
                .walkPlace(dto.getWalkPlace())
                .status(dto.getStatus())
                .matching(dto.getMatching())
                .schedulStart(dto.getSchedulStart())
                .schedulEnd(dto.getSchedulEnd())
                .build();
    }


}
//    default MatchingRoom matchinroomdtoEntity(MatchingRoomDTO dto) {
//        return MatchingRoom.builder()
//                .host(User.builder().userId(dto.getHostId()).build())
//                .user(User.builder().userId(dto.getUserId()).build())
//                .title(dto.getTitle())
//                .description(dto.getDescription())
//                .place(dto.getPlace())
//                .meetingDate(dto.getMeetingDate())
//                .meetingTime(dto.getMeetingTime())
//                .build();
//    }



