package com.busanit501.teamboot.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController// 데이터만 제공.
@Log4j2
public class SampleRestController {

    @PostMapping("/hiRest")
    // 서버 에서 문자열 배열 (클래스) -> 직렬화,-> 문자열로 변환
    // -> 중간 데이터 타입 JSON , XML
    // 형식으로 자동으로 변환해서 전달이 되었다.
    // 자동으로 변환해주는 도구 jackson 라이브러리,
    public String[] hiRest() {

        return new String[]{"aaa","bbb","ccc"};
    }
}
