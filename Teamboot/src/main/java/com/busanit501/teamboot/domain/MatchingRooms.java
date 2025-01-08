package com.busanit501.teamboot.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
//@ToString(exclude ="imageSet")
public class MatchingRooms extends BaseEntity {

    @Id // PK, 기본키,
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;

    private Long hostId;

    private Long userId;

    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;

    private String title;

    private String description;

    private Long max_participants;

    private Long current_participants;

    @Enumerated(EnumType.STRING)
    private RoomStatus roomStatus;

    private LocalDate matchingDate;

    private String address;

    public enum UserStatus {
        PENDING,
        ACCEPTED,
        REJECTED
    }
    public enum RoomStatus {
        PENDING,
        ACCEPTED,
        REJECTED
    }
}
