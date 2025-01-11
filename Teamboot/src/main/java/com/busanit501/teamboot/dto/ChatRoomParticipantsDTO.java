package com.busanit501.teamboot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRoomParticipantsDTO {
    private long roomParticipantsId;
    private long chatRoomId;
    private String senderId;
}
