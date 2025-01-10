package com.busanit501.teamboot.service;

import com.busanit501.teamboot.domain.ChatRoomParticipants;
import com.busanit501.teamboot.domain.ChatingRoom;
import com.busanit501.teamboot.domain.Member;
import com.busanit501.teamboot.dto.ChatRoomParticipantsDTO;
import com.busanit501.teamboot.dto.ChatingRoomDTO;
import com.busanit501.teamboot.repository.ChatMemberRepository;
import com.busanit501.teamboot.repository.ChatRoomParticipantsRepository;
import com.busanit501.teamboot.repository.ChatingRoomRepository;
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
public class ChatingRoomServiceImpl implements ChatingRoomService {
    @Autowired
    private ChatingRoomRepository chatingRoomRepository;
    @Autowired
    private ChatMemberRepository chatMemberRepository;
    @Autowired
    private ChatRoomParticipantsRepository chatRoomParticipantsRepository;
    private final ModelMapper modelMapper;

//    @Override
//    public long addChatingRoom(ChatingRoomDTO chatingRoomDTO, ChatRoomParticipantsDTO chatRoomParticipantsDTO) {
//        log.info("ChatingRoomServiceImpl chatingRoomDTO "+ chatingRoomDTO);
//        ChatingRoom chatingRoom = modelMapper.map(chatingRoomDTO, ChatingRoom.class);
//        ChatRoomParticipants roomParticipants = modelMapper.map(chatRoomParticipantsDTO, ChatRoomParticipants.class);
//
//        log.info("ChatingRoomServiceImpl chatingRoomDTO.getHostId(): "+ chatingRoomDTO.getHostId());
//        Member host = chatMemberRepository.findByMid(chatingRoomDTO.getHostId())
//                .orElseThrow(() -> new IllegalArgumentException("Invalid hostId: " + chatingRoomDTO.getHostId()));
//
//        chatingRoom.setHost(host);
//        log.info("ChatingRoomServiceImpl chatingRoom: "+ chatingRoom);
//        chatingRoom.setRoomId(null);
//        // 매칭룸 저장
//        ChatingRoom savedRoom = chatingRoomRepository.save(chatingRoom);
//
//        // RoomParticipants에 host 정보 추가
//        roomParticipants.setChatRoom(savedRoom);
//        roomParticipants.setSender(host);
//
//        // RoomParticipants 저장
//        chatRoomParticipantsRepository.save(roomParticipants);
//
//        return savedRoom.getRoomId();
//    }


@Override
@Transactional
public long addChatingRoom(ChatingRoomDTO chatingRoomDTO, List<ChatRoomParticipantsDTO> chatRoomParticipantsDTOList) {
    log.info("ChatingRoomServiceImpl chatingRoomDTO " + chatingRoomDTO);
    ChatingRoom chatingRoom = modelMapper.map(chatingRoomDTO, ChatingRoom.class);

    // Host 정보 처리
    Member host = chatMemberRepository.findByMid(chatingRoomDTO.getHostId())
            .orElseThrow(() -> new IllegalArgumentException("Invalid hostId: " + chatingRoomDTO.getHostId()));

    chatingRoom.setHost(host);
    log.info("ChatingRoomServiceImpl chatingRoom: " + chatingRoom);
    chatingRoom.setRoomId(null);  // 방 ID를 null로 설정

    // 매칭룸 저장
    ChatingRoom savedRoom = chatingRoomRepository.save(chatingRoom);

    log.info("ChatingRoomServiceImpl chatRoomParticipantsDTOList :" + chatRoomParticipantsDTOList);
    // 여러 참여자 정보 처리
    for (ChatRoomParticipantsDTO chatRoomParticipantsDTO : chatRoomParticipantsDTOList) {
        ChatRoomParticipants roomParticipants = modelMapper.map(chatRoomParticipantsDTO, ChatRoomParticipants.class);
        roomParticipants.setChatRoom(savedRoom);

        // 참여자마다 sender를 설정 (호스트일 경우 host로 설정, 나머지는 senderId에 맞게 설정)
        if (chatRoomParticipantsDTO.getSenderId().equals(chatingRoomDTO.getHostId())) {
            roomParticipants.setSender(host);  // Host 정보 추가
        } else {
            Member participant = chatMemberRepository.findByMid(chatRoomParticipantsDTO.getSenderId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid participantId: " + chatRoomParticipantsDTO.getSenderId()));
            roomParticipants.setSender(participant);  // 참여자 정보 설정
        }

        // RoomParticipants 저장
        chatRoomParticipantsRepository.save(roomParticipants);
    }

    return savedRoom.getRoomId();
}





    @Override
    public void updateChatingRoom(ChatingRoomDTO chatingRoomDTO) {
        Optional<ChatingRoom> result = chatingRoomRepository.findById((int) chatingRoomDTO.getRoomId());
        ChatingRoom chatingRoom = result.orElseThrow();
        chatingRoom.ChatingRoomUpdate(chatingRoomDTO.getTitle(),
                chatingRoomDTO.getDescription(),
                chatingRoomDTO.getMaxParticipants(),
                chatingRoomDTO.getStatus());
        chatingRoomRepository.save(chatingRoom);
    }
    @Override
    public void exitChatingRoom(ChatingRoomDTO chatingRoomDTO){
        Optional<ChatingRoom> result = chatingRoomRepository.findById((int) chatingRoomDTO.getRoomId());
        ChatingRoom chatingRoom = result.orElseThrow();
        chatingRoom.exitRoom(chatingRoomDTO.getCurrentParticipants());
        chatingRoomRepository.save(chatingRoom);
    }

    //유저 초대
    @Override
    public void inviteChatingRoom(ChatingRoomDTO chatingRoomDTO, ChatRoomParticipantsDTO chatRoomParticipantsDTO) {
        // 채팅방 정보를 가져옴
        Optional<ChatingRoom> result = chatingRoomRepository.findById((int) chatingRoomDTO.getRoomId());
        ChatingRoom chatingRoom = result.orElseThrow();

        // 현재 참가자 수를 업데이트: 현재 참가자 수에 초대된 사람 수만큼 증가
        chatingRoom.inviteRoom(chatingRoom.getCurrentParticipants()); // inviteRoom 메소드를 호출하여 참가자 수 증가

        // 채팅방 정보 저장
        chatingRoomRepository.save(chatingRoom);

        // 초대된 유저를 참가자로 추가
        ChatRoomParticipants participant = ChatRoomParticipants.builder()
                .chatRoom(chatingRoom)  // 초대된 채팅방 정보 설정
                .sender(Member.builder()
                        .mid(chatRoomParticipantsDTO.getSenderId()) // 유저 ID로 엔티티 빌드
                        .build())
                .build();


        // 참가자 정보 저장
        chatRoomParticipantsRepository.save(participant);
    }



    @Override
    public void deleteChatingRoom(long roomId) {
        chatingRoomRepository.deleteById((int) roomId);
    }

    @Override
    public void deleteRoomParticipants(long roomId, String userId) {
        chatRoomParticipantsRepository.deleteByRoomIdAndUserId(roomId, userId);
    }

    @Override
    public List<ChatingRoomDTO> searchAllChatingRoom(String keyword, String userId) {
        // 키워드로 매칭룸 검색
        List<ChatingRoom> chatingRooms = chatingRoomRepository.searchAllChatingRoom(keyword,userId);
        log.info("ChatingRoomServiceImpl searchAllChatingRoom "+ chatingRooms);
        // 검색 결과를 DTO로 변환
        List<ChatingRoomDTO> dtoList = new ArrayList<>();
        for (ChatingRoom chatingRoom : chatingRooms) {
            ChatingRoomDTO dto = ChatingRoomDTO.builder()
                    .roomId(chatingRoom.getRoomId())
                    .hostId(chatingRoom.getHost().getMid()) // User 엔티티에서 hostId 추출
                    .title(chatingRoom.getTitle())
                    .description(chatingRoom.getDescription())
                    .maxParticipants(chatingRoom.getMaxParticipants())
                    .currentParticipants(chatingRoom.getCurrentParticipants())
                    .status(chatingRoom.getStatus())
                    .build();
            dtoList.add(dto);
        }

        return dtoList;
    }
}
