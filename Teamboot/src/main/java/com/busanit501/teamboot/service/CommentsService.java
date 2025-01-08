package com.busanit501.teamboot.service;

import com.busanit501.teamboot.domain.Comments;
import com.busanit501.teamboot.domain.Member;

import java.util.List;

public interface CommentsService {
    List<Comments> getCommentsByCommunityId(Long communityId);
    Comments createComment(Long communityId, Member member, String content);
    void deleteComment(Long commentsId, Member member);
}
