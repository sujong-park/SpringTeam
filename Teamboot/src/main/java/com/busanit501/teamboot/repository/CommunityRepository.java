package com.busanit501.teamboot.repository;

import com.busanit501.bootproject.domain.Category;
import com.busanit501.bootproject.domain.Community;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CommunityRepository extends JpaRepository<Community, Long> {

    // 게시글 목록 조회 (User와 댓글 수 포함)
    @Query("SELECT c, m, COUNT(co.commentsId) " +
            "FROM Community c " +
            "LEFT JOIN c.member m " +
            "LEFT JOIN c.comments co " +
            "GROUP BY c, m ORDER BY c.communityId DESC")
    Page<Object[]> findAllWithMemberAndCommentsCount(Pageable pageable);

    Page<Community> findByCategory(Category category, Pageable pageable);
}
