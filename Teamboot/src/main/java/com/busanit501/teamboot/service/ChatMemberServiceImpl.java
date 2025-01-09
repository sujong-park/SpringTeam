package com.busanit501.teamboot.service;

import com.busanit501.teamboot.domain.Member;
import com.busanit501.teamboot.dto.MemberDTO;
import com.busanit501.teamboot.repository.ChatMemberRepository;
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

    @Override
    public List<MemberDTO> searchInviteUser(String keyword, long roomId) {
        log.info("searchInviteUser keyword: " + keyword);
        log.info("searchInviteUser roomId: " + roomId);
        List<Member> members = chatMemberRepository.searchInviteUserList(keyword, roomId);
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
