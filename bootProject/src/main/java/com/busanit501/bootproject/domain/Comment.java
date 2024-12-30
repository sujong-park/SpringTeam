package com.busanit501.bootproject.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Getter
@Setter
public class Comment extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @Column(columnDefinition = "TEXT")
    private String content;


    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

//
//    @ManyToOne
//    @JoinColumn(name = "user_id", nullable = false)
//    private User user;
}