package com.busanit501.teamboot.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * RoomParticipant 엔티티 클래스
 * - 매칭방에 참가하는 회원과 펫의 정보를 관리하는 엔티티로, 데이터베이스의 room_participants 테이블과 매핑된다.
 * - 매칭방, 참가 회원, 참가 회원의 펫, 참가 상태 정보를 포함한다.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "room_participants")
public class RoomParticipant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participant_id")
    private Long participantId;
    // 참가자의 고유 식별자 (Primary Key)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matching_room_id", nullable = false)
    private MatchingRoom matchingRoom;
    // 매칭방 정보 (MatchingRoom 엔티티와 다대일 관계)
    // 매칭방 ID로 매핑

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mid", nullable = false)
    private Member member;
    // 참가 회원 정보 (Member 엔티티와 다대일 관계)
    // 참가 회원 ID로 매핑

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;
    // 참가 회원의 반려동물 정보 (Pet 엔티티와 다대일 관계)
    // 참가 펫 ID로 매핑

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ParticipantStatus status;
    // 참가자의 상태 (Enum)
    // Pending: 대기 중, Accepted: 승인됨, Rejected: 거절됨

    /**
     * 참가 상태를 정의하는 열거형
     * - Pending: 참가 요청이 대기 상태.
     * - Accepted: 참가 요청이 승인된 상태.
     * - Rejected: 참가 요청이 거절된 상태.
     */
    public enum ParticipantStatus {
        Pending,
        Accepted,
        Rejected
    }
}
