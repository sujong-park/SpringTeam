package com.busanit501.teamboot.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
//@ToString(exclude ="imageSet")
public class ChatRoomParticipants extends BaseEntity {

    @Id // PK, 기본키,
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomParticipantsId;

    private Long chatRoomId;

    private Long senderId;

    @Enumerated(EnumType.STRING)
    private Status Status;
    
    public enum Status {
        Open,
        CLOSED
    }

}
