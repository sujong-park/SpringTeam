package com.busanit501.teamboot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRoomAllRegisterDTO {
    private ChatingRoomDTO chatingRoomDTO;
    private List<ChatRoomParticipantsDTO> chatRoomParticipantsDTO;
}
