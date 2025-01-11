package com.busanit501.teamboot.dto;

import com.busanit501.bootproject.domain.ChatingRoom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatingRoomDTO {

    private long roomId;
    private String hostId;
    private String title;
    private String description;
    private long maxParticipants;
    private long currentParticipants;
    private ChatingRoom.Status status;
}
