package com.busanit501.teamboot.service;

import com.busanit501.bootproject.domain.Member;
import com.busanit501.bootproject.dto.MemberDTO;
import com.busanit501.bootproject.repository.ChatMemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class ChatMemberServiceImpl implements ChatMemberService {

    @Autowired
    private final ChatMemberRepository chatMemberRepository;

    // 초대할 유저 검색
    @Override
    public List<MemberDTO> searchInviteUser(String keyword, long roomId) {
        log.info("searchInviteUser keyword: " + keyword);
        log.info("searchInviteUser roomId: " + roomId);

        // 리포지토리에서 회원 리스트를 검색
        List<Member> members = chatMemberRepository.searchInviteUserList(keyword, roomId);
        log.info("searchInviteUser members: " + members);

        // 검색된 회원들을 MemberDTO 객체로 변환하여 반환할 리스트 준비
        List<MemberDTO> dtoList = new ArrayList<>();
        for(Member member : members){
            MemberDTO dto = MemberDTO.builder()
                    .mid(member.getMid())
                    .name(member.getName())
                    .build();
            dtoList.add(dto);
        }
        return dtoList;
    }

    // 유저 검색 (채팅방을 생성한 유저 검색)
    @Override
    public List<MemberDTO> searchCreateUser(String keyword, String userId) {
        log.info("searchInviteUser keyword: " + keyword);
        log.info("searchInviteUser roomId: " + userId);
        List<Member> members = chatMemberRepository.searchCreateUserList(keyword, userId);
        log.info("searchInviteUser members: " + members);
        List<MemberDTO> dtoList = new ArrayList<>();
        for(Member member : members){
            MemberDTO dto = MemberDTO.builder()
                    .mid(member.getMid())
                    .name(member.getName())
                    .build();
            dtoList.add(dto);
        }
        return dtoList;
    }
}
