package com.busanit501.teamboot.service;

import com.busanit501.teamboot.domain.Pet;
import com.busanit501.teamboot.repository.PetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * PetService 클래스
 * - Pet 엔티티 관련 비즈니스 로직을 처리.
 * - PetRepository를 통해 데이터 접근.
 */
@Service
public class PetService {

    private final PetRepository petRepository;

    /**
     * 생성자 주입 방식으로 PetRepository 초기화
     *
     * @param petRepository PetRepository 객체
     */
    @Autowired
    public PetService(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    /**
     * 회원 ID로 모든 펫 조회
     *
     * @param memberId 회원 ID
     * @return 펫 목록
     */
    public List<Pet> findAllByMemberId(String memberId) {
        return petRepository.findAllByMember_Mid(memberId);
    }

    /**
     * 펫 ID로 펫 조회
     *
     * @param petId 펫 ID
     * @return 펫 Optional 객체
     */
    public Optional<Pet> findById(Long petId) {
        return petRepository.findById(petId);
    }

    /**
     * 회원 ID로 펫 유형을 포함한 펫 목록 조회
     *
     * @param memberId 회원 ID
     * @return 펫 목록
     */
    public List<Pet> getPetsWithType(String memberId) {
        return petRepository.findAllByMember_Mid(memberId); // Pet 엔티티에 type 포함
    }
}
