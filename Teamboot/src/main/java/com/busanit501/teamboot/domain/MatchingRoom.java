package com.busanit501.teamboot.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MatchingRoom extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;

    private String title;

    private String description;

    private String place;

    private LocalDate meetingDate;

    private LocalTime meetingTime;

    private Long maxParticipants;

    private String profilePicture;

    // 수정: FetchType.EAGER로 변경하거나, EntityGraph를 사용하여 member를 즉시 로딩
    @ManyToOne(fetch = FetchType.EAGER) // FetchType.LAZY -> EAGER로 변경
    @JoinColumn(name = "mid") // 외래키 컬럼명 설정
    private Member member;

    @OneToMany(mappedBy = "matchingRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoomParticipant> participants;

    // 기타 필요한 필드 및 메서드


/**
     * 현재 승인된 참가자 수를 반환하는 메서드
     *
     * @return 승인된 참가자의 수
     */
public Long getCurrentParticipants() {
    return participants.stream()
            .filter(p -> p.getStatus() == RoomParticipant.ParticipantStatus.Accepted)
            .map(p -> p.getMember().getMid()) // memberId로 매핑
            .distinct()
            .count();
}

}
