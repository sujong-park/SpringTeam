package com.busanit501.teamboot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageDTO {
    private long messageId;
    private long chatRoomId;
    private String senderId;
    private String content;
    private Timestamp sentAt;
    private boolean isRead;

    private String senderName;
}
