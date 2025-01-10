package com.busanit501.teamboot.repository;

import com.busanit501.teamboot.domain.Comments;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentsRepository extends JpaRepository<Comments, Long> {
    Page<Comments> findByCommunityCommunityId(Long communityId, Pageable pageable);
}