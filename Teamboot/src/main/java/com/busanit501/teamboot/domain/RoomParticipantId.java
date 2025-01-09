package com.busanit501.teamboot.domain;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

/**
 * RoomParticipantId 클래스
 * - 매칭방 참가자 엔티티(RoomParticipant)의 복합 키를 정의하는 클래스.
 * - JPA에서 복합 키를 사용할 때 @Embeddable 어노테이션을 통해 키 클래스를 정의.
 */
@Data
@Embeddable
public class RoomParticipantId implements Serializable {

    /**
     * 매칭방 ID
     * - 참가자가 속한 매칭방의 고유 ID.
     */
    private Long roomId;

    /**
     * 회원 ID
     * - 참가자 회원의 고유 ID.
     */
    private String memberId;

    /**
     * 반려동물 ID
     * - 참가자가 등록한 반려동물의 고유 ID.
     */
    private Long petId;

    /**
     * 기본 생성자
     * - JPA 표준에서는 기본 생성자가 필수.
     * - 리플렉션을 통해 엔티티를 초기화하거나 데이터베이스에서 값을 로드할 때 사용.
     */
    public RoomParticipantId() {}

    /**
     * 모든 필드를 초기화하는 생성자
     * @param roomId 매칭방 ID
     * @param memberId 회원 ID
     * @param petId 반려동물 ID
     */
    public RoomParticipantId(Long roomId, String memberId, Long petId) {
        this.roomId = roomId;
        this.memberId = memberId;
        this.petId = petId;
    }

    /**
     * equals 메서드 오버라이드
     * - 두 RoomParticipantId 객체를 비교하여 동일한 키를 가지는지 확인.
     * - roomId, memberId, petId 필드를 기준으로 비교.
     * @param o 비교할 객체
     * @return 두 객체가 동일한 키를 가지면 true, 그렇지 않으면 false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // 동일한 객체인 경우
        if (o == null || getClass() != o.getClass()) return false; // 타입이 다르거나 null인 경우
        RoomParticipantId that = (RoomParticipantId) o;
        return Objects.equals(roomId, that.roomId) &&
                Objects.equals(memberId, that.memberId) &&
                Objects.equals(petId, that.petId);
    }

    /**
     * hashCode 메서드 오버라이드
     * - 객체를 해시 기반 컬렉션(Map, Set 등)에서 사용할 때 고유한 해시 코드를 생성.
     * - roomId, memberId, petId를 기반으로 해시 코드를 생성.
     * @return 객체의 고유한 해시 코드
     */
    @Override
    public int hashCode() {
        return Objects.hash(roomId, memberId, petId);
    }
}
