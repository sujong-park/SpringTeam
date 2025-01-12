package com.busanit501.teamboot.dto;

import lombok.Data;

/**
 * 매칭방 생성자 정보 DTO
 */
@Data
public class MatchingUserDTO {
    private String mid; // 회원 ID
    private String name; // 회원 이름
    private String email; // 회원 이메일
    // 필요한 경우 추가 필드
}
