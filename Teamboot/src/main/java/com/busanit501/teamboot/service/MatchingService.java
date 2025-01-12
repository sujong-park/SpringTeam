package com.busanit501.teamboot.service;

import com.busanit501.teamboot.domain.MatchingRoom;
import com.busanit501.teamboot.domain.Member;
import com.busanit501.teamboot.domain.Pet;
import com.busanit501.teamboot.domain.RoomParticipant;
import com.busanit501.teamboot.dto.MatchingRoomDTO;
import com.busanit501.teamboot.dto.MatchingUserDTO;
import com.busanit501.teamboot.dto.PetDTO;
import com.busanit501.teamboot.exception.ResourceNotFoundException;
import com.busanit501.teamboot.repository.MatchingRoomRepository;
import com.busanit501.teamboot.repository.MemberRepository;
import com.busanit501.teamboot.repository.PetRepository;
import com.busanit501.teamboot.repository.RoomParticipantRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Service
public class MatchingService {

    private final MatchingRoomRepository roomRepository;
    private final RoomParticipantRepository participantRepository;
    private final PetRepository petRepository;
    private final MemberRepository memberRepository; // MemberRepository 추가

    @Autowired
    public MatchingService(MatchingRoomRepository roomRepository,
                           RoomParticipantRepository participantRepository,
                           PetRepository petRepository,
                           MemberRepository memberRepository) {
        this.roomRepository = roomRepository;
        this.participantRepository = participantRepository;
        this.petRepository = petRepository;
        this.memberRepository = memberRepository;
    }

    public List<MatchingRoomDTO> getAllRooms() {
        List<MatchingRoom> rooms = roomRepository.findAll();

        // 원본 리스트를 복사하여 역순으로 정렬
        List<MatchingRoom> reversedRooms = new ArrayList<>(rooms);
        Collections.reverse(reversedRooms);

        // 역순 정렬된 리스트를 DTO로 변환 후 반환
        return reversedRooms.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public MatchingRoom getRoomById(Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("매칭방을 찾을 수 없습니다. ID: " + roomId));
    }

    /**
     * 검색어를 포함한 매칭방 리스트 반환
     * 검색 범위: 제목, 장소, 펫 타입
     */
    public List<MatchingRoomDTO> getRoomsByQuery(String query) {
        // 검색된 MatchingRoom 엔티티 리스트를 가져옴
        List<MatchingRoom> rooms = roomRepository.searchRoomsByQuery(query);

        // 각 엔티티를 DTO로 변환
        return rooms.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 상위 5개의 키워드 추출 (제목, 장소, 펫 타입에서)
     */
    public List<String> getTopKeywords(int limit) {
        List<String> titles = roomRepository.findAllTitles();
        List<String> places = roomRepository.findAllPlaces();
        List<String> petTypes = roomRepository.findAllPetTypes();

        Map<String, Long> wordCount = new HashMap<>();

        // 불용어(stopwords) 정의 (필요에 따라 추가/수정)
        Set<String> stopwords = Set.of("the", "and", "is", "at", "which", "on", "a", "an",
                "을", "를", "에", "의", "는", "이", "가");

        // 제목에서 단어 추출
        extractWords(titles, wordCount, stopwords);

        // 장소에서 단어 추출
        extractWords(places, wordCount, stopwords);

        // 펫 타입에서 단어 추출
        extractWords(petTypes, wordCount, stopwords);

        // 빈도수 기준 내림차순 정렬 후 상위 'limit' 단어 반환
        return wordCount.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder()))
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * 단어 추출 및 빈도수 계산 메서드
     */
    private void extractWords(List<String> sources, Map<String, Long> wordCount, Set<String> stopwords) {
        for (String text : sources) {
            if (text != null) {
                // 소문자 변환, 특수문자 제거, 공백 기준 분리
                String[] words = text.toLowerCase().replaceAll("[^a-z0-9가-힣 ]", "").split("\\s+");
                for (String word : words) {
                    if (!stopwords.contains(word) && word.length() > 1) { // 불용어 및 한 글자 단어 제외
                        wordCount.put(word, wordCount.getOrDefault(word, 0L) + 1);
                    }
                }
            }
        }
    }

    @Transactional
    public void createRoom(MatchingRoomDTO dto, Member member) {
        // 새 매칭방 생성
        MatchingRoom room = new MatchingRoom();
        room.setMember(member); // User -> Member 변경
        room.setTitle(dto.getTitle());
        room.setDescription(dto.getDescription());
        room.setPlace(dto.getPlace());
        room.setMeetingDate(dto.getMeetingDate());
        room.setMeetingTime(dto.getMeetingTime());
        room.setMaxParticipants(dto.getMaxParticipants());

        room.setProfilePicture(dto.getProfilePicture());

        MatchingRoom savedRoom = roomRepository.save(room);

        // 호스트 펫들 등록
        List<Pet> pets = petRepository.findAllById(dto.getPetIds());
        for (Pet pet : pets) {
            RoomParticipant participant = new RoomParticipant();
            participant.setMatchingRoom(savedRoom);
            participant.setMember(member); // User -> Member 변경
            participant.setPet(pet);
            participant.setStatus(RoomParticipant.ParticipantStatus.Accepted);
            participantRepository.save(participant);
        }
    }

    @Transactional
    public void updateRoom(Long roomId, MatchingRoomDTO dto, Member member) {
        MatchingRoom room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("매칭방을 찾을 수 없습니다."));

        if (!room.getMember().getMid().equals(member.getMid())) { // User -> Member 변경
            throw new RuntimeException("방장만 수정할 수 있습니다.");
        }

        // 기본 정보 갱신
        room.setTitle(dto.getTitle());
        room.setDescription(dto.getDescription());
        room.setPlace(dto.getPlace());
        room.setMeetingDate(dto.getMeetingDate());
        room.setMeetingTime(dto.getMeetingTime());
        room.setMaxParticipants(dto.getMaxParticipants());

        room.setProfilePicture(dto.getProfilePicture());
        roomRepository.save(room);
        // 호스트의 펫 참가 정보 다시 세팅(기존 호스트 펫 정보는 모두 삭제 후 새로 등록)
        List<RoomParticipant> existingParticipants = participantRepository.findAllByMatchingRoomAndMember(room, member);
        participantRepository.deleteAll(existingParticipants);

        List<Pet> pets = petRepository.findAllById(dto.getPetIds());
        for (Pet pet : pets) {
            RoomParticipant participant = new RoomParticipant();
            participant.setMatchingRoom(room);
            participant.setMember(member); // User -> Member 변경
            participant.setPet(pet);
            participant.setStatus(RoomParticipant.ParticipantStatus.Accepted);
            participantRepository.save(participant);
        }
        // 수정된 room은 트랜잭션 종료 시점에 자동으로 DB 반영
    }

    @Transactional
    public void deleteRoom(Long roomId) {
        MatchingRoom room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("매칭방을 찾을 수 없습니다."));

        // 방에 속한 참가자 정보 삭제
        participantRepository.deleteAllByMatchingRoom(room);

        // 매칭방 삭제
        roomRepository.delete(room);
    }

    public List<Member> getAcceptedParticipantsByRoomId(Long roomId) {
        List<RoomParticipant> participants = participantRepository.findAllByMatchingRoom_RoomIdAndStatus(
                roomId, RoomParticipant.ParticipantStatus.Accepted);
        return participants.stream()
                .map(RoomParticipant::getMember) // User -> Member 변경
                .distinct()
                .collect(Collectors.toList());
    }

    // 참가 신청
    @Transactional
    public void applyRoom(Long roomId, String memberId, List<Long> petIds) {
        log.info("Applying for roomId: {}, memberId: {}, petIds: {}", roomId, memberId, petIds);

        if (petIds == null || petIds.isEmpty()) {
            throw new RuntimeException("적어도 하나의 반려동물을 선택해야 합니다.");
        }

        MatchingRoom room = getRoomById(roomId);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));

        // 호스트의 펫을 제외하고, 추가로 신청하는 펫들이 사용자의 소유인지 확인
        List<Pet> userPets = petRepository.findAllById(petIds);
        if (userPets.size() != petIds.size()) {
            throw new ResourceNotFoundException("일부 펫을 찾을 수 없습니다.");
        }

        // 이미 신청했는지 확인 (기존 Pending 또는 Accepted 상태)
        List<RoomParticipant> existingParticipants = participantRepository.findAllByMatchingRoomAndMember(room, member);
        if (!existingParticipants.isEmpty()) {
            throw new RuntimeException("이미 참가 신청을 했습니다.");
        }

        // 최대 인원 확인 (호스트 포함)
        long acceptedParticipants = participantRepository.countDistinctMemberByMatchingRoomAndStatus(
                room, RoomParticipant.ParticipantStatus.Accepted);
        log.info("Current accepted participants: {}, Max participants: {}", acceptedParticipants, room.getMaxParticipants());
        if (acceptedParticipants + 1 > room.getMaxParticipants()) {
            throw new RuntimeException("참가 인원이 초과되었습니다.");
        }

        // Pending 상태로 참가 신청
        for (Pet pet : userPets) {
            RoomParticipant participant = new RoomParticipant();
            participant.setMatchingRoom(room);
            participant.setMember(member); // User -> Member 변경
            participant.setPet(pet);
            participant.setStatus(RoomParticipant.ParticipantStatus.Pending);
            participantRepository.save(participant);
            log.info("Saved RoomParticipant: {}", participant);
        }
    }

    // 참가자 승인
    @Transactional
    public void acceptParticipant(Long roomId, String memberId) {
        MatchingRoom room = getRoomById(roomId);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));

        List<RoomParticipant> participants = participantRepository.findAllByMatchingRoomAndMember(room, member);
        if (participants.isEmpty()) {
            throw new ResourceNotFoundException("참가 신청을 찾을 수 없습니다.");
        }

        long acceptedParticipants =
                participantRepository.countDistinctMemberByMatchingRoomAndStatus(room, RoomParticipant.ParticipantStatus.Accepted);
        if (acceptedParticipants + 1 > room.getMaxParticipants()) {
            throw new RuntimeException("최대 참가 인원을 초과하여 승인할 수 없습니다.");
        }

        for (RoomParticipant participant : participants) {
            if (participant.getStatus() == RoomParticipant.ParticipantStatus.Accepted) {
                throw new RuntimeException("이미 승인된 참가 신청입니다.");
            }
            participant.setStatus(RoomParticipant.ParticipantStatus.Accepted);
            participantRepository.save(participant);
        }
    }

    // 참가자 거절
    @Transactional
    public void rejectParticipant(Long roomId, String memberId) {
        MatchingRoom room = getRoomById(roomId);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));

        List<RoomParticipant> participants = participantRepository.findAllByMatchingRoomAndMember(room, member);
        if (participants.isEmpty()) {
            throw new ResourceNotFoundException("참가 신청을 찾을 수 없습니다.");
        }

        for (RoomParticipant participant : participants) {
            if (participant.getStatus() == RoomParticipant.ParticipantStatus.Rejected) {
                throw new RuntimeException("이미 거절된 참가 신청입니다.");
            }
            participant.setStatus(RoomParticipant.ParticipantStatus.Rejected);
            participantRepository.save(participant);
        }
    }

    /**
     * MatchingRoom -> MatchingRoomDTO 변환
     */
    public MatchingRoomDTO convertToDto(MatchingRoom room) {
        MatchingRoomDTO dto = new MatchingRoomDTO();
        dto.setRoomId(room.getRoomId());
        dto.setTitle(room.getTitle());
        dto.setDescription(room.getDescription());
        dto.setPlace(room.getPlace());
        dto.setMeetingDate(room.getMeetingDate());
        dto.setMeetingTime(room.getMeetingTime());
        dto.setMaxParticipants(room.getMaxParticipants());
        dto.setProfilePicture(room.getProfilePicture());

        // 호스트 펫 ID 리스트 추가
        List<Long> petIds = room.getParticipants().stream()
                .filter(p -> p.getMember().getMid().equals(room.getMember().getMid()))
                .map(p -> p.getPet().getPetId())
                .collect(Collectors.toList());
        dto.setPetIds(petIds);

        // 참여자 펫 정보 리스트 추가
        List<PetDTO> pets = room.getParticipants().stream()
                .map(participant -> {
                    PetDTO petDTO = new PetDTO();
                    petDTO.setPetId(participant.getPet().getPetId());
                    petDTO.setName(participant.getPet().getName());
                    petDTO.setType(participant.getPet().getType());
//                    petDTO.setBirth(participant.getPet().getBirth());
                    petDTO.setGender(participant.getPet().getGender());
                    petDTO.setWeight(participant.getPet().getWeight());
                    petDTO.setPersonality(participant.getPet().getPersonality());
                    return petDTO;
                })
                .collect(Collectors.toList());
        dto.setPets(pets);

        // petType 필드 설정 (모든 펫 타입을 쉼표로 구분)
        String petTypes = pets.stream()
                .map(PetDTO::getType)
                .distinct()
                .collect(Collectors.joining(", "));
        dto.setPetType(petTypes);

        // 현재 참가 인원 수 계산 및 설정
        long currentParticipants = room.getParticipants().stream()
                .filter(p -> p.getStatus() == RoomParticipant.ParticipantStatus.Accepted)
                .count();
        dto.setCurrentParticipants(currentParticipants);

        // 호스트 정보 설정
        dto.setMember(convertMemberToDto(room.getMember())); // User -> Member 변경

        return dto;
    }

    /**
     * Member 도메인 객체를 MatchingUserDTO로 변환하는 메서드
     */
    public MatchingUserDTO convertMemberToDto(Member member) {
        MatchingUserDTO dto = new MatchingUserDTO();
        dto.setMid(member.getMid());
        dto.setName(member.getName());
        dto.setEmail(member.getEmail());
        // 필요한 다른 필드 설정
        return dto;
    }

    /**
     * 특정 사용자가 매칭방에 신청했는지 여부를 확인하는 메서드
     *
     * @param room 매칭방
     * @param member 사용자
     * @return 신청 여부 (true: 신청함, false: 신청하지 않음)
     */
    public boolean hasMemberApplied(MatchingRoom room, Member member) {
        List<RoomParticipant> participants = participantRepository.findAllByMatchingRoomAndMember(room, member);
        for (RoomParticipant participant : participants) {
            if (participant.getStatus() == RoomParticipant.ParticipantStatus.Pending ||
                    participant.getStatus() == RoomParticipant.ParticipantStatus.Accepted) {
                return true;
            }
        }
        return false;
    }

    public List<RoomParticipant> getParticipantsByRoomId(Long roomId) {
        MatchingRoom room = getRoomById(roomId);
        return participantRepository.findAllByMatchingRoom_RoomId(roomId);
    }

    /**
     * 1) 특정 매칭방 ID에 대해,
     * 상태가 Accepted인 RoomParticipant 목록을 반환한다.
     */
    public List<RoomParticipant> getAcceptedParticipants(Long roomId) {
        MatchingRoom room = getRoomById(roomId);
        return participantRepository.findAllByMatchingRoomAndStatus(
                room, RoomParticipant.ParticipantStatus.Accepted);
    }

    /**
     * 2) member->petList 구조로 반환 (Accepted)
     */
    public Map<Member, List<Pet>> getAcceptedMemberPets(MatchingRoom room) {
        List<RoomParticipant> accepted = participantRepository
                .findAllByMatchingRoomAndStatus(room, RoomParticipant.ParticipantStatus.Accepted);

        Map<Member, List<Pet>> map = new LinkedHashMap<>();
        log.info("Accepted participants count: {}", accepted.size());
        for (RoomParticipant rp : accepted) {
            Member m = rp.getMember();
            Pet p = rp.getPet();
            map.computeIfAbsent(m, k -> new ArrayList<>()).add(p);
        }
        return map;
    }

    @Transactional(readOnly = true)
    public Map<Member, List<Pet>> getPendingMemberPets(MatchingRoom room) {
        // 1) 해당 방(room)에 대해 상태가 Pending 인 participant 조회
        List<RoomParticipant> pendingList =
                participantRepository.findAllByMatchingRoomAndStatus(room, RoomParticipant.ParticipantStatus.Pending);

        // 2) Map<Member, List<Pet>>
        Map<Member, List<Pet>> pendingMap = new LinkedHashMap<>();
        for (RoomParticipant rp : pendingList) {
            Member member = rp.getMember();
            Pet pet = rp.getPet();
            pendingMap.computeIfAbsent(member, k -> new ArrayList<>()).add(pet);
        }
        return pendingMap;
    }

    public List<RoomParticipant> filterParticipants(List<RoomParticipant> participants,
                                                    RoomParticipant.ParticipantStatus status) {
        return participants.stream()
                .filter(p -> p.getStatus() == status)
                .collect(Collectors.toList());
    }
}
