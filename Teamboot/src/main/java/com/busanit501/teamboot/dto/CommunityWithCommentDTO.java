package com.busanit501.teamboot.dto;

import com.busanit501.teamboot.domain.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class CommunityWithCommentDTO {
    private Long communityId;
    private String title;
    private String content;
    private Category category;
    private String memberName;
//    private String memberAddress;
    private LocalDateTime created_at;
    private Long commentsCount;
}
