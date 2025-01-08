package com.busanit501.teamboot.repository;

import com.busanit501.teamboot.domain.Comments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comments, Long> {
    List<Comments> findByCommunityCommunityId(Long communityId);
}