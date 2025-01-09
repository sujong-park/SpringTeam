package com.busanit501.teamboot.service;

import com.busanit501.teamboot.dto.MemberDTO;

import java.util.List;

public interface ChatMemberService {
    List<MemberDTO> searchInviteUser(String keyword, long roomId);
    List<MemberDTO> searchCreateUser(String keyword, String userId);
}
