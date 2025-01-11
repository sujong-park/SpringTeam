package com.busanit501.teamboot.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityDTO {
    private Long communityId;
    private Long userId;
    private String userName;
    private MemberJoinDTO member;
    private String category;
    private String title;
    private String content;
    private String imageUrl;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    private List<CommentsDTO> comments;
}