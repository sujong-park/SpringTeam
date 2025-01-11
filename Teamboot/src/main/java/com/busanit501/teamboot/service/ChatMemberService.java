package com.busanit501.teamboot.service;

import com.busanit501.bootproject.dto.MemberDTO;

import java.util.List;

public interface ChatMemberService {
    //채팅방에 포함되어 있지 않은 유저 목록 조회
    List<MemberDTO> searchInviteUser(String keyword, long roomId);

    //로그인한 본인을 제외한 유저 목록 조회
    List<MemberDTO> searchCreateUser(String keyword, String userId);
}
