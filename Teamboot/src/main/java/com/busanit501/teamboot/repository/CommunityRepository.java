package com.busanit501.teamboot.repository;

import com.busanit501.teamboot.domain.Category;
import com.busanit501.teamboot.domain.Community;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CommunityRepository extends JpaRepository<Community, Long> {

    // 게시글 목록 조회 (User와 댓글 수 포함)
    @Query("SELECT c, u, COUNT(ct) " +
            "FROM Community c " +
            "LEFT JOIN FETCH c.member u " +  // Member 정보 즉시 로딩
            "LEFT JOIN Comments ct ON ct.community.communityId = c.communityId " +
            "GROUP BY c, u")  // Member도 Group by에 추가
    Page<Object[]> findAllWithMemberAndCommentsCount(Pageable pageable);

    Page<Community> findByCategory(Category category, Pageable pageable);
}
