package com.busanit501.teamboot.repository;

import com.busanit501.teamboot.domain.Member;
import com.busanit501.teamboot.domain.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * PetRepository 인터페이스
 * Pet 엔티티에 대한 CRUD 및 커스텀 쿼리 메서드를 정의합니다.
 */
@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {

    /**
     * 회원과 기본 펫을 조회하는 메서드
     *
     * @param member    회원 엔티티
     * @param isDefault 기본 펫 여부
     * @return 펫 Optional 객체
     */
    Optional<Pet> findByMemberAndIsDefault(Member member, Boolean isDefault);

    /**
     * 회원 ID로 모든 펫을 조회하는 메서드
     *
     * @param mid 회원 ID
     * @return 펫 목록
     */
    List<Pet> findAllByMember_Mid(String mid);

    // List<Pet> findAllByUserId(Long userId); // 제거됨
}
