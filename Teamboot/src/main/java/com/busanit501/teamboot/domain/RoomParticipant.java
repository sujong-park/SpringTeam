package com.busanit501.teamboot.domain;

import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "room_participants")
public class RoomParticipant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participant_id")
    private Long participantId; // 참가자 고유 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matching_room_id", nullable = false)
    private MatchingRoom matchingRoom; // 참가하는 매칭방 정보

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mid", nullable = false)
    private Member member; // 참가자 회원 정보

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet; // 참가자의 반려동물 정보

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ParticipantStatus status; // 참가 상태 (Pending, Accepted, Rejected)

    /**
     * 참가 상태를 나타내는 열거형
     */
    public enum ParticipantStatus {
        Pending,   // 대기 중
        Accepted,  // 승인됨
        Rejected   // 거절됨
    }
}
