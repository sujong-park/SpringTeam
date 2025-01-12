package com.busanit501.teamboot.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
//@ToString(exclude ="imageSet")
public class Pet extends BaseEntity {

    @Id // PK, 기본키,
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long petId;

    private String name;

    private String type;

    private LocalDate birth;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String personality;
    
    private Float weight;

    private String profile_picture;

    @Builder.Default
    private boolean verified = false;

    @Column(name = "is_default")
    private Boolean isDefault;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mid") // 외래 키 컬럼 이름
    private Member member;

}
