package com.busanit501.teamboot.repository;

import com.busanit501.teamboot.domain.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

//Member -> @Id -> String mid -> <Member, String>
public interface MemberRepository extends JpaRepository<Member, String> {
    // 회원 조회, 추가로 MemberRole 테이블을 조인해서 , 소셜 로그인이 아닌 상태
    @EntityGraph(attributePaths = "roleSet")
    @Query("select m from Member m where m.mid = :mid and m.social = false")
    Optional<Member> getWithRoles(String mid);

    @EntityGraph(attributePaths = "roleSet")
    Optional<Member> findByMid(String mid);

    // 사용자 패스워드 변경하는 기능.
    @Modifying
    @Transactional
    @Query("update Member m set m.mpw = :mpw where m.mid = :mid")
    void updatePassword(@Param("mpw") String password, @Param("mid") String mid);
}
