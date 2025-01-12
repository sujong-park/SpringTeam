package com.busanit501.teamboot.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * 매칭방 데이터 전송 객체 (DTO)
 * 매칭방 생성 및 수정 시 사용됩니다.
 */
@Data
public class MatchingRoomDTO {
    // ★ 수정/생성 시 방을 구분하기 위한 필드
    private Long roomId; // 매칭방 ID

    // 이미지 URL (업로드 시 저장 경로)
    private String profilePicture; // 매칭방 프로필 사진 URL

    @NotBlank(message = "제목은 필수 입력 항목입니다.")
    private String title; // 매칭방 제목

    @NotBlank(message = "설명은 필수 입력 항목입니다.")
    private String description; // 매칭방 설명

    @NotBlank(message = "장소는 필수 입력 항목입니다.")
    private String place; // 모임 장소

    @NotNull(message = "날짜는 필수 입력 항목입니다.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate meetingDate; // 모임 날짜

    @NotNull(message = "시간은 필수 입력 항목입니다.")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime meetingTime; // 모임 시간

    @NotNull(message = "최대 인원은 필수 입력 항목입니다.")
    @Min(value = 1, message = "최대 인원은 최소 1명 이상이어야 합니다.")
    private Long maxParticipants; // 최대 참가 인원

    private Long currentParticipants; // 현재 승인된 참가자 수

    @NotEmpty(message = "최소 한 마리의 반려동물을 선택해야 합니다.")
    private List<Long> petIds; // 호스트의 펫 ID 목록

    // 추가 참가 펫 ID 목록(선택적)
    private List<Long> additionalPetIds; // 추가로 참가하는 펫 ID 목록

    // 추가된 필드: 참여자의 펫 목록
    private List<PetDTO> pets; // 참여자의 펫 정보 목록

    // 펫 타입을 문자열로 추가 (여러 타입을 하나의 문자열로 결합)
    private String petType; // 펫 타입 문자열

    private MatchingUserDTO member; // 매칭방 생성자 정보

}
