package com.busanit501.teamboot.repository;

import com.busanit501.teamboot.domain.MatchingRoom;
import com.busanit501.teamboot.domain.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 매칭방 레포지토리 인터페이스
 * 매칭방 엔티티에 대한 데이터 접근을 처리합니다.
 */
public interface MatchingRoomRepository extends JpaRepository<MatchingRoom, Long> {

    /**
     * 모든 매칭방을 회원 정보와 함께 조회하는 커스텀 메서드
     *
     * @return 매칭방 리스트
     */
    @EntityGraph(attributePaths = {"member"})
    @Query("SELECT r FROM MatchingRoom r")
    List<MatchingRoom> findAllWithMembers();

    /**
     * 검색어를 포함하는 매칭방을 회원 정보와 함께 제목, 장소, 펫 타입으로 검색하는 커스텀 메서드
     *
     * @param query 검색어
     * @return 검색된 매칭방 리스트
     */
    @EntityGraph(attributePaths = {"member"})
    @Query("SELECT DISTINCT r FROM MatchingRoom r " +
            "LEFT JOIN r.participants rp " +
            "LEFT JOIN rp.pet p " +
            "WHERE LOWER(r.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(r.place) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(p.type) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<MatchingRoom> searchRoomsByQueryWithMembers(@Param("query") String query);

    /**
     * 모든 매칭방의 제목을 조회하는 커스텀 쿼리
     *
     * @return 매칭방 제목 리스트
     */
    @Query("SELECT r.title FROM MatchingRoom r")
    List<String> findAllTitles();

    /**
     * 모든 매칭방의 장소를 조회하는 커스텀 쿼리
     *
     * @return 매칭방 장소 리스트
     */
    @Query("SELECT r.place FROM MatchingRoom r")
    List<String> findAllPlaces();

    /**
     * 모든 매칭방의 펫 타입을 조회하는 커스텀 쿼리
     *
     * @return 매칭방의 펫 타입 리스트
     */
    @Query("SELECT p.type FROM MatchingRoom r JOIN r.participants rp JOIN rp.pet p")
    List<String> findAllPetTypes();

    @Query("SELECT m FROM Member m LEFT JOIN FETCH m.roleSet WHERE m.email = :email")
    Optional<Member> getWithRoles(@Param("email") String email);
}
