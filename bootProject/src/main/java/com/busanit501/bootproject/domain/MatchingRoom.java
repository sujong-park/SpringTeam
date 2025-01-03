package com.busanit501.bootproject.domain;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Table(name = "MatchingRoom")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MatchingRoom extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int roomId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hostId")
    private User host;

    @Column(nullable = false, length = 255)
    private String title;

    private String description;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 10")
    private int maxParticipants;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 1")
    private int currentParticipants;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('Open', 'Closed') DEFAULT 'Open'")
    private RoomStatus status;

    @Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createdAt;

    public void MatchingRoomUpdate(String title,
                                   String description,
                                   int maxParticipants,
                                   RoomStatus status) {
        this.title = title;
        this.description = description;
        this.maxParticipants = maxParticipants;
        this.status = status;
    }

    public void exitRoom(int currentParticipants){
        this.currentParticipants = currentParticipants-1;
    }
}

