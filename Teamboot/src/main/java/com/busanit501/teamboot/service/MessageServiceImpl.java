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
    private final ModelMapper modelMapper;

    @Override
    public long addMessage(MessageDTO messageDTO) {
        Message message = modelMapper.map(messageDTO, Message.class);

        Member sender = chatMemberRepository.findByMid(messageDTO.getSenderId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid senderId: " + messageDTO.getSenderId()));
        ChatingRoom chatRoom = matchingRoomRepository.findById((int) messageDTO.getChatRoomId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid senderId: " + messageDTO.getChatRoomId()));
        message.setSender(sender);
        message.setChatRoom(chatRoom);

        Message savedMessage = messageRepository.save(message);
        return savedMessage.getMessageId();
    }

    @Override
    public void updateMessage(MessageDTO messageDTO) {
        Optional<Message> message = messageRepository.findById((int) messageDTO.getMessageId());
        Message savedMessage = message.orElseThrow();
        savedMessage.MessageUpdate(messageDTO.getContent());
        messageRepository.save(savedMessage);
    }

    @Override
    public void deleteMessage(long messageId) {
        messageRepository.deleteById((int) messageId);
    }

    @Override
    public void deleteAllMessagesByUser(String userId, long roomId) {
        messageRepository.deleteAllMessagesByUserId(userId, roomId);
    }

    @Override
    public List<MessageDTO> searchMessage(long roodId) {
        List<MessageDTO> messages = messageRepository.searchMessageByMatchingRoomId(roodId);

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
