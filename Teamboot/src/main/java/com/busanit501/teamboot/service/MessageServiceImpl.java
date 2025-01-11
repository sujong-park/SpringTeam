package com.busanit501.teamboot.service;

import com.busanit501.teamboot.domain.ChatingRoom;
import com.busanit501.teamboot.domain.Member;
import com.busanit501.teamboot.domain.Message;
import com.busanit501.teamboot.dto.MessageDTO;
import com.busanit501.teamboot.repository.ChatMemberRepository;
import com.busanit501.teamboot.repository.ChatRoomParticipantsRepository;
import com.busanit501.teamboot.repository.ChatingRoomRepository;
import com.busanit501.teamboot.repository.MessageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class MessageServiceImpl implements MessageService {

    @Autowired
    private final MessageRepository messageRepository;

    @Autowired
    private ChatMemberRepository chatMemberRepository;

    @Autowired
    private ChatRoomParticipantsRepository roomParticipantsRepository;

    @Autowired
    private ChatingRoomRepository matchingRoomRepository;

    // DTO와 Entity 간 변환을 위한 객체
    private final ModelMapper modelMapper;

    //채팅 보내기
    @Override
    public long addMessage(MessageDTO messageDTO) {
        // MessageDTO를 Message 엔티티로 변환
        Message message = modelMapper.map(messageDTO, Message.class);

        // 보내는 사람 정보 조회 (보낸 사람의 회원 정보)
        Member sender = chatMemberRepository.findByMid(messageDTO.getSenderId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid senderId: " + messageDTO.getSenderId()));

        // 채팅방 정보 조회 (채팅방 ID로 채팅방 정보 조회)
        ChatingRoom chatRoom = matchingRoomRepository.findById((int) messageDTO.getChatRoomId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid senderId: " + messageDTO.getChatRoomId()));

        // 메시지에 보내는 사람과 채팅방 설정
        message.setSender(sender);
        message.setChatRoom(chatRoom);

        // 메시지 저장
        Message savedMessage = messageRepository.save(message);
        return savedMessage.getMessageId();
    }

    //채팅 업데이트(사용 안함)
    @Override
    public void updateMessage(MessageDTO messageDTO) {
        Optional<Message> message = messageRepository.findById((int) messageDTO.getMessageId());
        Message savedMessage = message.orElseThrow();
        savedMessage.MessageUpdate(messageDTO.getContent());
        messageRepository.save(savedMessage);
    }

    //채팅 삭제(로그인한 본인 채팅 내역만)
    @Override
    public void deleteMessage(long messageId) {
        // 메시지 ID로 해당 메시지를 삭제
        messageRepository.deleteById((int) messageId);
    }

    //채팅방 나갈때, 유저의 대화 내용 전부 삭제
    @Override
    public void deleteAllMessagesByUser(String userId, long roomId) {
        // 유저 ID와 채팅방 ID로 해당 유저의 모든 메시지를 삭제
        messageRepository.deleteAllMessagesByUserId(userId, roomId);
    }

    //채팅내역 조회(채팅방 기준)
    @Override
    public List<MessageDTO> searchMessage(long roodId) {
        // 채팅방 ID로 메시지 목록 조회
        List<MessageDTO> messages = messageRepository.searchMessageByMatchingRoomId(roodId);

        // 조회된 메시지를 DTO 리스트로 변환
        List<MessageDTO> dtoList = new ArrayList<>();
        for(MessageDTO message : messages) {
            MessageDTO dto = MessageDTO.builder()
                    .messageId(message.getMessageId())
                    .chatRoomId(message.getChatRoomId())
                    .senderId(message.getSenderId())
                    .content(message.getContent())
                    .sentAt(message.getSentAt())
                    .isRead(message.isRead())
                    .senderName(message.getSenderName())
                    .build();
            dtoList.add(dto);
        }
        return dtoList;
    }
}
