package com.busanit501.teamboot.repository;

import com.busanit501.teamboot.domain.ChatingRoom;
import com.busanit501.teamboot.domain.Member;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@Log4j2
public class ChatMemberRepositoryTests {
    @Autowired
    ChatMemberRepository chatMemberRepository;

    @Test
    @Transactional
    public void searchFindByMidTest() {
        Optional<Member> result = chatMemberRepository.findByMid("test");
        Member member = result.get();
        log.info(member);
    }

    @Test
    @Transactional
    public void searchCreateChatRoomUser() {
        String keyword = "";
        String userId = "test5";
        List<Member> members =  chatMemberRepository.searchCreateUserList(keyword, userId);
        log.info("조회된 유저 정보 :" + members);
    }
}
