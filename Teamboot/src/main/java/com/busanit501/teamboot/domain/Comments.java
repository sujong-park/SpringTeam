package com.busanit501.teamboot.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comments extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentsId;

    @ManyToOne
//    @JoinColumn(name = "community_id", nullable = false)
    private Community community;

    @ManyToOne
//    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

//    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    public void updateContent(String content) {
        this.content = content;
    }
}
