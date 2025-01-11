package com.busanit501.teamboot.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
//@ToString(exclude ="imageSet")
public class ChatingRoom extends BaseEntity {

    @Id // PK, 기본키,
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hostId")
    private Member host;

    @Column(nullable = false, length = 255)
    private String title;

    private String description;

    @Column(nullable = false, columnDefinition = "LONG DEFAULT 10")
    private Long maxParticipants;

    @Column(nullable = false, columnDefinition = "LONG DEFAULT 1")
    private Long currentParticipants;

    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {
        Open,
        CLOSED
    }

    @OneToMany(mappedBy = "chatRoom",
            cascade = CascadeType.ALL
            ,fetch = FetchType.LAZY,
            orphanRemoval = true)
    @BatchSize(size = 20)
    @Builder.Default
    private Set<Message> messageSet = new HashSet<>();

    @OneToMany(mappedBy = "chatRoom",
            cascade = CascadeType.ALL
            ,fetch = FetchType.LAZY,
            orphanRemoval = true)
    @BatchSize(size = 20)
    @Builder.Default
    private Set<ChatRoomParticipants> participantSet = new HashSet<>();

    public void ChatingRoomUpdate(String title,
                                  String description,
                                  long maxParticipants,
                                  Status status) {
        this.title = title;
        this.description = description;
        this.maxParticipants = maxParticipants;
        this.status = status;
    }

    public void exitRoom(long currentParticipants){
        this.currentParticipants = currentParticipants - 1;
    }
    public void inviteRoom(long currentParticipants){
        this.currentParticipants = currentParticipants + 1;
    }
}
