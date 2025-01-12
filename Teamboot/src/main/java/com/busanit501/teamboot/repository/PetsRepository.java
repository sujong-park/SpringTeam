package com.busanit501.teamboot.repository;

import com.busanit501.teamboot.domain.Pet;
import com.busanit501.teamboot.repository.search.PetsSearch;
import org.springframework.data.jpa.repository.JpaRepository;

//extends JpaRepository<Pet, Long> -> 기본 쿼리 메소드 이용해서,
// 간단한 crud 디비 작업은, 메서드를 이용해서 처리가 가능함.

// Querydsl 이용시, 만들었던 인터페이스를 추가 구현 해야함.
public interface PetsRepository extends JpaRepository<Pet, Long> , PetsSearch {
//public interface PetRepository extends JpaRepository<Pet, Long>  {
    // 아무 메서드가 없음.
    // 하지만, 우리는 기본 탑재된 쿼리 메소드를 활용할 예정.

//    //쿼리스트링 ,방법1
//    Page<Pet> findByTitleContainingOrderByBnoDesc(String title, Pageable pageable);
//
//    //@Query , 방법2 전달. JPQL 문법으로, 작성하고, dialect 방언,
//    // 모든 디비(마리아다비, 오라클, 마이SQL, PostGre 관계형 디비)에 적용이 됨.
//    @Query("select g from Pet g where g.content like concat('%',:keyword,'%')")
//    Page<Pet> findByKeyword(String keyword, Pageable pageable);
//
//    // Querydsl  도구 이용해서, 방법3,
//    // PetSearch 인터페이스 구현하고, 이 인터페이스를 구현한 클래스에서 문법 사용.
//    // PetSearchImpl 구현한 클래스의 이름. 구현체,
//
//    // 방법2,에서 JPQL, 디비에 상관없이 작성도 되지만,
//    // 반대로, 특정 디비의 문법으로 만 작성도 가능.
//    // nativeQuery = true
//    @Query(value = "select now()" , nativeQuery = true)
//    String now();
//
//    // 조회시, 특정의 속성을 같이 조회를 함. -> 조인,
//    @EntityGraph(attributePaths = {"imageSet"})
//    @Query("select g from Pet g where g.petId=:petId")
//    Optional<Pet> findByIdWithImages(Long petId);


}
