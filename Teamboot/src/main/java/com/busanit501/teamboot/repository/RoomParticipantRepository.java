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

@Repository
public interface RoomParticipantRepository extends JpaRepository<RoomParticipant, Long> {
    @Modifying
    @Query("DELETE FROM RoomParticipant rp WHERE rp.matchingRoom = :room AND rp.member = :member")
    void deleteAllByMatchingRoomAndMember(@Param("room") MatchingRoom room, @Param("member") Member member);

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

    long countDistinctMemberByMatchingRoomAndStatus(MatchingRoom room, RoomParticipant.ParticipantStatus status);

    List<RoomParticipant> findAllByMatchingRoom(MatchingRoom room);
    void deleteAllByMatchingRoom(MatchingRoom matchingRoom);

    // MatchingRoom ID와 ParticipantStatus를 기반으로 참가자 조회
    List<RoomParticipant> findAllByMatchingRoom_RoomIdAndStatus(Long roomId, RoomParticipant.ParticipantStatus status);

    List<RoomParticipant> findAllByMatchingRoomAndStatus(MatchingRoom room, RoomParticipant.ParticipantStatus participantStatus);
}
