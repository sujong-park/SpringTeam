package com.busanit501.teamboot.dto;

import com.busanit501.teamboot.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberJoinDTO {
    private String mid;
    private String mpw;
    private String email;
    private boolean del;
    private boolean social;


    // toEntity() 메서드 추가
    public Member toEntity() {
        return Member.builder()
                .mid(this.mid)
                .mpw(this.mpw)
                .email(this.email)
                .del(this.del)
                .social(this.social)
//                .gender(Gender.MALE)  // Gender 필드는 기본값을 설정 (필요에 따라 수정 가능)// 기본 ratingCount 값 (필요에 따라 수정 가능)
                .build();
    }

    // fromEntity() 메서드 추가
    public static MemberJoinDTO fromEntity(Member member) {
        return MemberJoinDTO.builder()
                .mid(member.getMid())
                .mpw(member.getMpw())
                .email(member.getEmail())
                .del(member.isDel())
                .social(member.isSocial())
                .build();
    }


}
