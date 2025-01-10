package com.busanit501.teamboot.service;


import com.busanit501.teamboot.dto.MessageDTO;

import java.util.List;

public interface MessageService {
    //채팅 보내기
    long addMessage(MessageDTO messageDTO);

    //채팅 업데이트(사용 안함)
    void updateMessage(MessageDTO messageDTO);

    //채팅 삭제(로그인한 본인 채팅 내역만)
    void deleteMessage(long messageId);

    //채팅방 나갈때, 유저의 대화 내용 전부 삭제
    void deleteAllMessagesByUser(String userId,long roomId);

    //채팅내역 조회(채팅방 기준)
    List<MessageDTO> searchMessage(long roodId);
}
