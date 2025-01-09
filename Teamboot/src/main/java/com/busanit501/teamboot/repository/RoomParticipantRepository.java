package com.busanit501.teamboot.repository;

import com.busanit501.teamboot.domain.MatchingRoom;
import com.busanit501.teamboot.domain.RoomParticipant;
import com.busanit501.teamboot.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * RoomParticipantRepository 인터페이스
 * - RoomParticipant 엔티티에 대한 CRUD 및 커스텀 쿼리 메서드를 정의.
 * - JpaRepository를 확장하여 기본적인 데이터 접근 기능을 제공.
 */
@Repository
public interface RoomParticipantRepository extends JpaRepository<RoomParticipant, Long> {

    /**
     * 특정 매칭방과 회원에 대한 참가자 목록을 조회합니다.
     *
     * @param matchingRoom 매칭방 객체
     * @param member       회원 객체
     * @return 참가자 목록
     */
    List<RoomParticipant> findAllByMatchingRoomAndMember(MatchingRoom matchingRoom, Member member);

    /**
     * 특정 매칭방에 대한 참가자 수를 세어 반환합니다.
     *
     * @param matchingRoom 매칭방 객체
     * @return 참가자 수
     */
    long countByMatchingRoom(MatchingRoom matchingRoom);

    /**
     * 특정 매칭방 ID로 모든 참가자 목록을 조회합니다.
     *
     * @param roomId 매칭방 ID
     * @return 참가자 목록
     */
    List<RoomParticipant> findAllByMatchingRoom_RoomId(Long roomId);

    /**
     * 특정 매칭방과 참가자 상태에 대한 고유한 회원 수를 세어 반환합니다.
     *
     * @param matchingRoom 매칭방 객체
     * @param status        참가자 상태 (ParticipantStatus)
     * @return 고유한 회원 수
     */
    long countDistinctMemberByMatchingRoomAndStatus(MatchingRoom matchingRoom, RoomParticipant.ParticipantStatus status);

    /**
     * 특정 매칭방과 참가자 상태에 따른 참가자 목록을 조회합니다.
     *
     * @param matchingRoom      매칭방 객체
     * @param participantStatus 참가자 상태 (ParticipantStatus)
     * @return 참가자 목록
     */
    List<RoomParticipant> findAllByMatchingRoomAndStatus(MatchingRoom matchingRoom, RoomParticipant.ParticipantStatus participantStatus);
}
