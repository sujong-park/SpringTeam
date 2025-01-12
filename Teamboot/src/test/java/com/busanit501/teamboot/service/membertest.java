package com.busanit501.teamboot.service;


import com.busanit501.teamboot.domain.Member;
import com.busanit501.teamboot.dto.MemberDTO;
import com.busanit501.teamboot.dto.MemberJoinDTO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Log4j2
public class membertest {


//    @Test
//    public void testMemberDTOToEntityConversion() {
//        // MemberDTO 더미 데이터 생성
//        MemberDTO memberDTO = MemberDTO.builder()
//                .mid("testUser01@gmail.com")
//                .name("홍길동")
//                .build();
//
//        // MemberDTO를 Member 엔티티로 변환
//        Member member = memberDTO.toEntity();
//
////        // 변환된 엔티티의 값이 예상대로 설정되었는지 확인
////        assertNotNull(member);
////        assertEquals("testUser01", member.getMid());
////        assertEquals("홍길동", member.getName());
//    }

//    @Test
//    public void testEntityToMemberDTOConversion() {
//        // Member 엔티티 더미 데이터 생성
//        Member member = Member.builder()
//                .mid("test2@test.com")
//                .mpw("test")
//                .build();
//
//        // Member 엔티티를 MemberDTO로 변환
//        MemberDTO memberDTO = MemberDTO.fromEntity(member);
//
//        // 변환된 DTO의 값이 예상대로 설정되었는지 확인
////        assertNotNull(memberDTO);
////        assertEquals("testUser02", memberDTO.getMid());
////        assertEquals("김철수", memberDTO.getName());
//    }

    @Test
    public void testMemberJoinDTOToEntityConversion() {
        // MemberJoinDTO 더미 데이터 생성
        MemberJoinDTO memberJoinDTO = MemberJoinDTO.builder()
                .mid("test2")
                .mpw("password123")
                .email("newuser01@example.com")
                .del(false)
                .social(false)
                .build();

        // MemberJoinDTO를 Member 엔티티로 변환
        Member member = memberJoinDTO.toEntity();  // 이 메서드는 MemberJoinDTO에 추가해야 합니다.

    }

    @Test
    public void testEntityToMemberJoinDTOConversion() {
        // Member 엔티티 더미 데이터 생성
        Member member = Member.builder()
                .mid("existingUser01")
                .mpw("password456")
                .email("existinguser01@example.com")
                .del(false)
                .social(true)
                .build();

        // Member 엔티티를 MemberJoinDTO로 변환
        MemberJoinDTO memberJoinDTO = MemberJoinDTO.fromEntity(member);  // 이 메서드는 MemberJoinDTO에 추가해야 합니다.

        // 변환된 DTO의 값이 예상대로 설정되었는지 확인
//        assertNotNull(memberJoinDTO);
//        assertEquals("existingUser01", memberJoinDTO.getMid());
//        assertEquals("password456", memberJoinDTO.getMpw());
//        assertEquals("existinguser01@example.com", memberJoinDTO.getEmail());
//        assertFalse(memberJoinDTO.isDel());
//        assertTrue(memberJoinDTO.isSocial());
    }
}
