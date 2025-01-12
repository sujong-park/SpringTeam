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

/**
 * 매칭 서비스 클래스
 * 매칭방 관련 비즈니스 로직을 처리합니다.
 */
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

    /**
     * 모든 매칭방을 조회하여 DTO 리스트로 반환하는 메서드
     *
     * @return 매칭방 DTO 리스트
     */
    public List<MatchingRoomDTO> getAllRooms() {
        List<MatchingRoom> rooms = roomRepository.findAllWithMembers();
        List<MatchingRoom> reversedRooms = new ArrayList<>(rooms);
        Collections.reverse(reversedRooms);
        return reversedRooms.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 특정 ID의 매칭방을 조회하는 메서드
     *
     * @param roomId 매칭방 ID
     * @return 매칭방 엔티티
     * @throws ResourceNotFoundException 매칭방을 찾을 수 없을 때
     */
    public MatchingRoom getRoomById(Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("매칭방을 찾을 수 없습니다. ID: " + roomId));
    }


    /**
     * 검색어를 포함한 매칭방 리스트를 반환하는 메서드
     * 검색 범위: 제목, 장소, 펫 타입
     *
     * @param query 검색어
     * @return 검색된 매칭방 DTO 리스트
     */
    public List<MatchingRoomDTO> getRoomsByQuery(String query) {
        List<MatchingRoom> rooms = roomRepository.searchRoomsByQueryWithMembers(query);
        return rooms.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 상위 N개의 키워드를 추출하여 반환하는 메서드
     * 키워드 출처: 제목, 장소, 펫 타입
     *
     * @param limit 상위 키워드 개수 제한
     * @return 상위 키워드 리스트
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
     *
     * @param sources    텍스트 리스트
     * @param wordCount  단어 빈도수 맵
     * @param stopwords 불용어 집합
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

    /**
     * 매칭방을 생성하는 메서드
     *
     * @param dto    매칭방 DTO
     * @param member 매칭방 생성자 회원
     */
    @Transactional
    public void createRoom(MatchingRoomDTO dto, Member member) {
        // 새 매칭방 생성
        MatchingRoom room = new MatchingRoom();
        room.setMember(member); // 호스트 설정
        room.setTitle(dto.getTitle());
        room.setDescription(dto.getDescription());
        room.setPlace(dto.getPlace());
        room.setMeetingDate(dto.getMeetingDate());
        room.setMeetingTime(dto.getMeetingTime());
        room.setMaxParticipants(dto.getMaxParticipants());

        room.setProfilePicture(dto.getProfilePicture());

        MatchingRoom savedRoom = roomRepository.save(room); // 매칭방 저장

        // 호스트 펫들 등록
        List<Pet> pets = petRepository.findAllById(dto.getPetIds());
        for (Pet pet : pets) {
            RoomParticipant participant = new RoomParticipant();
            participant.setMatchingRoom(savedRoom);
            participant.setMember(member); // 호스트 회원 설정
            participant.setPet(pet);
            participant.setStatus(RoomParticipant.ParticipantStatus.Accepted); // 호스트는 자동 승인됨
            participantRepository.save(participant); // 참가자 저장
        }
    }

    /**
     * 매칭방을 업데이트하는 메서드
     *
     * @param roomId 매칭방 ID
     * @param dto    매칭방 DTO
     * @param member 매칭방 수정자 회원
     */
    @Transactional
    public void updateRoom(Long roomId, MatchingRoomDTO dto, Member member) {
        MatchingRoom room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("매칭방을 찾을 수 없습니다."));

        if (!room.getMember().getMid().equals(member.getMid())) { // 호스트인지 확인
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
        roomRepository.save(room); // 매칭방 업데이트

        // 호스트의 펫 참가 정보 다시 세팅 (기존 호스트 펫 정보는 모두 삭제 후 새로 등록)
        List<RoomParticipant> existingParticipants = participantRepository.findAllByMatchingRoomAndMember(room, member);
        participantRepository.deleteAll(existingParticipants);

        List<Pet> pets = petRepository.findAllById(dto.getPetIds());
        for (Pet pet : pets) {
            RoomParticipant participant = new RoomParticipant();
            participant.setMatchingRoom(room);
            participant.setMember(member); // 호스트 회원 설정
            participant.setPet(pet);
            participant.setStatus(RoomParticipant.ParticipantStatus.Accepted); // 호스트는 자동 승인됨
            participantRepository.save(participant); // 참가자 저장
        }
        // 수정된 room은 트랜잭션 종료 시점에 자동으로 DB 반영
    }

    /**
     * 매칭방을 삭제하는 메서드
     *
     * @param roomId 매칭방 ID
     */
    @Transactional
    public void deleteRoom(Long roomId) {
        MatchingRoom room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("매칭방을 찾을 수 없습니다."));

        // 방에 속한 참가자 정보 삭제
        participantRepository.deleteAllByMatchingRoom(room);

        // 매칭방 삭제
        roomRepository.delete(room);
    }

    /**
     * 특정 매칭방 ID에 대한 승인된 참가자 목록을 반환하는 메서드
     *
     * @param roomId 매칭방 ID
     * @return 승인된 참가자 회원 리스트
     */
    public List<Member> getAcceptedParticipantsByRoomId(Long roomId) {
        List<RoomParticipant> participants = participantRepository.findAllByMatchingRoom_RoomIdAndStatus(
                roomId, RoomParticipant.ParticipantStatus.Accepted);
        return participants.stream()
                .map(RoomParticipant::getMember) // 참가자 회원 정보 추출
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 참가 신청을 처리하는 메서드
     *
     * @param roomId    매칭방 ID
     * @param memberId  회원 ID
     * @param petIds    참가할 펫 ID 리스트
     */
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
            participant.setMember(member); // 참가자 회원 설정
            participant.setPet(pet);
            participant.setStatus(RoomParticipant.ParticipantStatus.Pending); // 대기 상태
            participantRepository.save(participant);
            log.info("Saved RoomParticipant: {}", participant);
        }
    }

    /**
     * 참가자를 승인하는 메서드
     *
     * @param roomId    매칭방 ID
     * @param memberId  승인할 회원 ID
     */
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
            participant.setStatus(RoomParticipant.ParticipantStatus.Accepted); // 상태 변경
            participantRepository.save(participant); // 참가자 업데이트
        }
    }

    /**
     * 참가자를 거절하는 메서드
     *
     * @param roomId    매칭방 ID
     * @param memberId  거절할 회원 ID
     */
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
            participant.setStatus(RoomParticipant.ParticipantStatus.Rejected); // 상태 변경
            participantRepository.save(participant); // 참가자 업데이트
        }
    }

    /**
     * 매칭방 엔티티를 DTO로 변환하는 메서드
     *
     * @param room 매칭방 엔티티
     * @return 매칭방 DTO
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
        dto.setMember(convertMemberToDto(room.getMember())); // 호스트 회원 정보 변환

        // 로그 추가: DTO의 member 정보 확인
        if (dto.getMember() == null) {
            log.error("MatchingRoomDTO의 member 정보가 null입니다. roomId: {}", room.getRoomId());
        } else {
            log.info("MatchingRoomDTO 변환 완료: roomId={}, member={}", room.getRoomId(), dto.getMember().getName());
        }

        return dto;
    }

    /**
     * Member 도메인 객체를 MatchingUserDTO로 변환하는 메서드
     *
     * @param member 회원 엔티티
     * @return 매칭유저 DTO
     */
    public MatchingUserDTO convertMemberToDto(Member member) {
        if (member == null) {
            log.warn("convertMemberToDto 호출 시 member가 null입니다.");
            return null;
        }
        MatchingUserDTO dto = new MatchingUserDTO();
        dto.setMid(member.getMid());
        dto.setName(member.getName());
        dto.setEmail(member.getEmail());
        return dto;
    }


    /**
     * 특정 사용자가 매칭방에 신청했는지 여부를 확인하는 메서드
     *
     * @param room   매칭방
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

    /**
     * 특정 매칭방 ID에 대한 모든 참가자 목록을 반환하는 메서드
     *
     * @param roomId 매칭방 ID
     * @return 참가자 목록
     */
    public List<RoomParticipant> getParticipantsByRoomId(Long roomId) {
        MatchingRoom room = getRoomById(roomId);
        return participantRepository.findAllByMatchingRoom_RoomId(roomId);
    }

    /**
     * 특정 매칭방 ID에 대해, 상태가 Accepted인 RoomParticipant 목록을 반환한다.
     *
     * @param roomId 매칭방 ID
     * @return 승인된 참가자 목록
     */
    public List<RoomParticipant> getAcceptedParticipants(Long roomId) {
        MatchingRoom room = getRoomById(roomId);
        return participantRepository.findAllByMatchingRoomAndStatus(
                room, RoomParticipant.ParticipantStatus.Accepted);
    }

    /**
     * 승인된 참가자들을 회원과 펫 리스트로 매핑하여 반환하는 메서드
     *
     * @param room 매칭방 엔티티
     * @return 회원과 펫 리스트의 매핑 맵
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

    /**
     * 대기 상태의 참가자들을 회원과 펫 리스트로 매핑하여 반환하는 메서드
     *
     * @param room 매칭방 엔티티
     * @return 회원과 펫 리스트의 매핑 맵
     */
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

    /**
     * 참가자 목록을 상태별로 필터링하는 메서드
     *
     * @param participants 참가자 목록
     * @param status        필터링할 상태
     * @return 필터링된 참가자 목록
     */
    public List<RoomParticipant> filterParticipants(List<RoomParticipant> participants,
                                                    RoomParticipant.ParticipantStatus status) {
        return participants.stream()
                .filter(p -> p.getStatus() == status)
                .collect(Collectors.toList());
    }
}
