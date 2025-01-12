package com.busanit501.teamboot.service;

import com.busanit501.teamboot.domain.Member;
import com.busanit501.teamboot.dto.MemberJoinDTO;

public interface MemberService {
    static class MidExistException extends Exception {

    }
    void join(MemberJoinDTO memberJoinDTO) throws MidExistException;


}
