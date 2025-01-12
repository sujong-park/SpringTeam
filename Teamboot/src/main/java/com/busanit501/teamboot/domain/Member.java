package com.busanit501.teamboot.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "roleSet") // 회원 각각의 권한, 일반, 관리자
public class Member extends BaseEntity {
    @Id
    private String mid;

    private String mpw;
    
    private String name;

    private String email;

    private LocalDate birth;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String address;


    private String profilePicture;

    private String phoneNumber;

    private Float rating;

    private Long ratingCount;

    private boolean del;
    private boolean social;

    @ElementCollection(fetch = FetchType.LAZY)
    @Builder.Default
    private Set<MemberRole> roleSet = new HashSet<>();


    // 멤버 값 교체하는 메서드 추가.
    public void changePassword(String mpw) {
        this.mpw = mpw;
    }

    public void changeEmail(String email) {
        this.email = email;
    }

    public void changeDel(boolean del) {
        this.del = del;
    }

    public void addRole(MemberRole memberRole) {
        this.roleSet.add(memberRole);
    }

    public void clearRole() {
        this.roleSet.clear();
    }

    public void changeSocial(boolean social) {
        this.social = social;
    }

}
