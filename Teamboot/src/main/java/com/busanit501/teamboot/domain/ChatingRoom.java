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
public class ChatingRoom extends BaseEntity {

    @Id // PK, 기본키,
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;

    private Long hostId;

    private String title;

    private String description;

    private Long maxParticipants;

    private Long currentParticipants;

    @Enumerated(EnumType.STRING)
    private Status Status;

    public enum Status {
        Open,
        CLOSED
    }

}
