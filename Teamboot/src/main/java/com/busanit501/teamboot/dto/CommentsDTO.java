package com.busanit501.teamboot.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentsDTO {
    private Long commentsId;
    private Long communityId;
    private Long memberId;
    private String memberName;
    private String content;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
}
