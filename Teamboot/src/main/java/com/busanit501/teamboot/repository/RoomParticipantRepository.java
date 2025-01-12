package com.busanit501.teamboot.repository;

import com.busanit501.teamboot.domain.MatchingRoom;
import com.busanit501.teamboot.domain.Member;
import com.busanit501.teamboot.domain.RoomParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 참가자 레포지토리 인터페이스
 * RoomParticipant 엔티티에 대한 데이터 접근을 처리합니다.
 */
@Repository
public interface RoomParticipantRepository extends JpaRepository<RoomParticipant, Long> {

    /**
     * 특정 매칭방과 회원에 해당하는 참가자들을 삭제하는 커스텀 쿼리
     *
     * @param room    매칭방 객체
     * @param member  회원 객체
     */
    @Modifying
    @Query("DELETE FROM RoomParticipant rp WHERE rp.matchingRoom = :room AND rp.member = :member")
    void deleteAllByMatchingRoomAndMember(@Param("room") MatchingRoom room, @Param("member") Member member);

    /**
     * 특정 매칭방과 회원에 대한 참가자 목록을 조회하는 메서드
     *
     * @param matchingRoom 매칭방 객체
     * @param member       회원 객체
     * @return 참가자 목록
     */
    List<RoomParticipant> findAllByMatchingRoomAndMember(MatchingRoom matchingRoom, Member member);

    /**
     * 특정 매칭방에 대한 참가자 수를 세어 반환하는 메서드
     *
     * @param matchingRoom 매칭방 객체
     * @return 참가자 수
     */
    long countByMatchingRoom(MatchingRoom matchingRoom);

    /**
     * 특정 매칭방 ID로 모든 참가자 목록을 조회하는 메서드
     *
     * @param roomId 매칭방 ID
     * @return 참가자 목록
     */
    List<RoomParticipant> findAllByMatchingRoom_RoomId(Long roomId);

    /**
     * 특정 매칭방과 상태로 중복 없는 회원 수를 세는 메서드
     *
     * @param room   매칭방 객체
     * @param status 참가 상태
     * @return 중복 없는 회원 수
     */
    long countDistinctMemberByMatchingRoomAndStatus(MatchingRoom room, RoomParticipant.ParticipantStatus status);

    /**
     * 특정 매칭방과 상태로 모든 참가자 목록을 조회하는 메서드
     *
     * @param room   매칭방 객체
     * @param status 참가 상태
     * @return 참가자 목록
     */
    List<RoomParticipant> findAllByMatchingRoomAndStatus(MatchingRoom room, RoomParticipant.ParticipantStatus status);

    /**
     * 매칭방 전체 참가자를 삭제하는 메서드
     *
     * @param matchingRoom 매칭방 객체
     */
    void deleteAllByMatchingRoom(MatchingRoom matchingRoom);

    /**
     * 매칭방 ID와 상태로 참가자 목록을 조회하는 메서드
     *
     * @param roomId 매칭방 ID
     * @param status 참가 상태
     * @return 참가자 목록
     */
    List<RoomParticipant> findAllByMatchingRoom_RoomIdAndStatus(Long roomId, RoomParticipant.ParticipantStatus status);
}
