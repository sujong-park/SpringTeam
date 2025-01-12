package com.busanit501.teamboot.domain;

import com.busanit501.teamboot.dto.CommunityDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "communities")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Community extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long communityId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    private Category category;

    private String title;
    private String content;

    @Column(length = 300)
    private String imageUrl;
    @Builder.Default
    private int commentCount = 0;

    @OneToMany(mappedBy = "community", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Comments> comments = new ArrayList<>();


    // 수정 메서드 엔티티 간 복사 및 업데이트
    public void updateFromEntity(Community updatedCommunity) {
        this.title = updatedCommunity.getTitle();
        this.content = updatedCommunity.getContent();
        this.category = updatedCommunity.getCategory();

        if (updatedCommunity.getImageUrl() != null) {
            this.imageUrl = updatedCommunity.getImageUrl();
        }
    }
    // 클라이언트에서 받은 DTO 데이터로 엔티티 업데이트
    public void updateFromDTO(CommunityDTO communityDTO) {
        this.title = communityDTO.getTitle();
        this.content = communityDTO.getContent();
        this.category = Category.valueOf(communityDTO.getCategory());
    }

    // 사진 수정
    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    }