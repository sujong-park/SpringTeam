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

    // DTO와 Entity 변환을 위한 ModelMapper
    private final ModelMapper modelMapper;

    //채팅방 생성(본인만 포함)
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

// 채팅방 생성(여러명 초대가능)
@Override
@Transactional
public long addChatingRoom(ChatingRoomDTO chatingRoomDTO, List<ChatRoomParticipantsDTO> chatRoomParticipantsDTOList) {
    log.info("ChatingRoomServiceImpl chatingRoomDTO " + chatingRoomDTO);
    // ChatingRoomDTO를 ChatingRoom 엔티티로 변환
    ChatingRoom chatingRoom = modelMapper.map(chatingRoomDTO, ChatingRoom.class);

    // Host 정보 처리 (hostId를 기준으로 Host Member 조회)
    Member host = chatMemberRepository.findByMid(chatingRoomDTO.getHostId())
            .orElseThrow(() -> new IllegalArgumentException("Invalid hostId: " + chatingRoomDTO.getHostId()));

    // 채팅방의 Host 설정
    chatingRoom.setHost(host);
    log.info("ChatingRoomServiceImpl chatingRoom: " + chatingRoom);
    // 방 ID를 null로 설정하여 새로운 방 생성
    chatingRoom.setRoomId(null);

    // 채팅방 저장
    ChatingRoom savedRoom = chatingRoomRepository.save(chatingRoom);

    // 여러 참여자 처리 (호스트 제외한 참여자들)
    log.info("ChatingRoomServiceImpl chatRoomParticipantsDTOList :" + chatRoomParticipantsDTOList);
    for (ChatRoomParticipantsDTO chatRoomParticipantsDTO : chatRoomParticipantsDTOList) {
        ChatRoomParticipants roomParticipants = modelMapper.map(chatRoomParticipantsDTO, ChatRoomParticipants.class);
        // 해당 참여자를 방에 설정
        roomParticipants.setChatRoom(savedRoom);

        // 참여자마다 sender를 설정 (호스트일 경우 host로 설정, 나머지는 senderId에 맞게 설정)
        if (chatRoomParticipantsDTO.getSenderId().equals(chatingRoomDTO.getHostId())) {
            // 호스트 정보 설정
            roomParticipants.setSender(host);
        } else {
            Member participant = chatMemberRepository.findByMid(chatRoomParticipantsDTO.getSenderId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid participantId: " + chatRoomParticipantsDTO.getSenderId()));
            // 참여자 정보 설정
            roomParticipants.setSender(participant);
        }
        // RoomParticipants 저장
        chatRoomParticipantsRepository.save(roomParticipants);
    }
    // 생성된 채팅방의 roomId 반환
    return savedRoom.getRoomId();
}
    //채팅방 수정(방장만 가능)
    @Override
    public void updateChatingRoom(ChatingRoomDTO chatingRoomDTO) {
        // 채팅방 ID로 채팅방을 찾아 수정
        Optional<ChatingRoom> result = chatingRoomRepository.findById((int) chatingRoomDTO.getRoomId());

        // 채팅방을 찾을 수 없으면 예외 발생
        ChatingRoom chatingRoom = result.orElseThrow();

        // 채팅방 정보 업데이트
        chatingRoom.ChatingRoomUpdate(chatingRoomDTO.getTitle(),
                chatingRoomDTO.getDescription(),
                chatingRoomDTO.getMaxParticipants(),
                chatingRoomDTO.getStatus());
        // 수정된 채팅방 저장
        chatingRoomRepository.save(chatingRoom);
    }
    @Override
    public void exitChatingRoom(ChatingRoomDTO chatingRoomDTO){
        // 채팅방 ID로 채팅방을 찾아서 나가기
        Optional<ChatingRoom> result = chatingRoomRepository.findById((int) chatingRoomDTO.getRoomId());

        // 채팅방을 찾을 수 없으면 예외 발생
        ChatingRoom chatingRoom = result.orElseThrow();

        // 방에서 나가기 처리
        chatingRoom.exitRoom(chatingRoomDTO.getCurrentParticipants());

        // 변경 사항 저장
        chatingRoomRepository.save(chatingRoom);
    }

    //채팅방에 초대(모든 유저 가능)
    @Override
    public void inviteChatingRoom(ChatingRoomDTO chatingRoomDTO, ChatRoomParticipantsDTO chatRoomParticipantsDTO) {
        // 채팅방 정보를 가져옴
        Optional<ChatingRoom> result = chatingRoomRepository.findById((int) chatingRoomDTO.getRoomId());

        // 채팅방을 찾을 수 없으면 예외 발생
        ChatingRoom chatingRoom = result.orElseThrow();

        // 초대된 사람 수만큼 참가자 수 업데이트
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

    //채팅방 나가기(방장을 제외한 유저만 가능)
    @Override
    public void deleteChatingRoom(long roomId) {
        // 채팅방 삭제
        chatingRoomRepository.deleteById((int) roomId);
    }
    @Override
    public void deleteRoomParticipants(long roomId, String userId) {
        // 채팅방에서 특정 유저 삭제
        chatRoomParticipantsRepository.deleteByRoomIdAndUserId(roomId, userId);
    }

    //채팅방 조회(로그인한 유저가 포함한 채팅방 조회)
    @Override
    public List<ChatingRoomDTO> searchAllChatingRoom(String keyword, String userId) {
        // 키워드로 매칭되는 채팅방 검색
        List<ChatingRoom> chatingRooms = chatingRoomRepository.searchAllChatingRoom(keyword,userId);
        log.info("ChatingRoomServiceImpl searchAllChatingRoom "+ chatingRooms);

        // 검색된 채팅방 리스트를 DTO로 변환
        List<ChatingRoomDTO> dtoList = new ArrayList<>();
        for (ChatingRoom chatingRoom : chatingRooms) {
            ChatingRoomDTO dto = ChatingRoomDTO.builder()
                    .roomId(chatingRoom.getRoomId()) // 채팅방 ID
                    .hostId(chatingRoom.getHost().getMid()) // 호스트 ID
                    .title(chatingRoom.getTitle()) // 채팅방 제목
                    .description(chatingRoom.getDescription()) // 채팅방 설명
                    .maxParticipants(chatingRoom.getMaxParticipants()) // 최대 참가자 수
                    .currentParticipants(chatingRoom.getCurrentParticipants()) // 현재 참가자 수
                    .status(chatingRoom.getStatus()) // 채팅방 상태
                    .build();
            dtoList.add(dto); // DTO 리스트에 추가
        }
        // DTO 리스트 반환
        return dtoList;
    }
}
