package com.busanit501.teamboot.dto;

import com.busanit501.teamboot.domain.Comments;
import com.fasterxml.jackson.annotation.JsonFormat;
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
    private String memberId;
    private String memberName;
    private String content;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime created_at;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")

    private LocalDateTime updated_at;

    public static CommentsDTO fromEntity(Comments comment) {
        return CommentsDTO.builder()
                .commentsId(comment.getCommentsId())
                .communityId(comment.getCommunity().getCommunityId())
                .memberId(comment.getMember().getMid()) // Member ID 가져오기
                .memberName(comment.getMember().getMid()) // Member 이름 가져오기
                .content(comment.getContent())
                .created_at(comment.getRegDate()) // 등록 날짜 가져오기
                .updated_at(comment.getModDate()) // 수정 날짜 가져오기
                .build();
    }
}
