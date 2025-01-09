package com.busanit501.teamboot.service;


import com.busanit501.teamboot.dto.MessageDTO;

import java.util.List;

public interface MessageService {
    long addMessage(MessageDTO messageDTO);
    void updateMessage(MessageDTO messageDTO);
    void deleteMessage(long messageId);
    void deleteAllMessagesByUser(String userId,long roomId);
    List<MessageDTO> searchMessage(long roodId);
}
