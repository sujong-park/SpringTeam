package com.busanit501.teamboot.controller;

import com.busanit501.teamboot.dto.PageRequestDTO;
import com.busanit501.teamboot.dto.PageResponseDTO;
import com.busanit501.teamboot.dto.ReplyDTO;
import com.busanit501.teamboot.service.ReplyService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/replies")
@Log4j2
@RequiredArgsConstructor
public class ReplyController {

    private final ReplyService replyService;

    //ReplyService 만들어서, 작업해야함.
    @Tag(name = "댓글 등록 post 방식",
            description = "댓글 등록을 진행함, post 형식으로")
    @PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE)
    // ResponseEntity -> 반환 타입으로 사용할 예정.
    // 전달 1) 데이터 2) http 상태 코드 같이 전달.
    // 예시)_ 댓글 작성 , http 200 ok

    // 설명
    // 화면 -> 서버
    // 화면에서 댓글 작성 값 -> json 형식 전달 -> 서버 전달.->
    // ->json -> 일반클래스 변환(ReplyDTO)변환,

    // 서버 -> 화면 , 전달,
    //Map<String,Long> 으로전달 -> jackson 직렬화 -> 문자열로 변환,
    // json 문자열 형식으로 변환.

    // @RequestBody, JSON 의 문자열로 전달을 받아요.
    // 원래의 일반 클래스로 역직렬화,
    // Jackson 컨버터 도구가, 알아서, 역직렬화 해줌.
    public ResponseEntity<Map<String,Long>> register(
            @Valid @RequestBody ReplyDTO replyDTO,
            BindingResult bindingResult
    ) throws BindException {
        log.info(" ReplyController replyDTO: ", replyDTO);
        // 확인용, 더미 데이터 ,

        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }
        Long rno = replyService.register(replyDTO);
        Map<String,Long> map = Map.of("rno",rno);
        return ResponseEntity.ok(map);
    }

    //댓글 목록 조회,
    // localhost:8080/replies/list/122
    @Tag(name = "댓글 목록 조회",description = "댓글 목록 조회 RESTful get방식")
    @GetMapping(value ="/list/{bno}")
    public PageResponseDTO<ReplyDTO> getList(@PathVariable("bno") Long bno, PageRequestDTO pageRequestDTO)
    {
        log.info(" ReplyController getList: bno={}", bno);
        PageResponseDTO<ReplyDTO> responseDTO = replyService.listWithReply(bno, pageRequestDTO);
        return responseDTO;
    }

    // 댓글 하나 조회, 상세보기
    // localhost:8080/replies/{rno:댓글번호}
    @Tag(name = "댓글 하나 조회",description = "댓글 하나 조회 RESTful get방식")
    @GetMapping(value ="/{rno}")
    public ReplyDTO getRead(@PathVariable("rno") Long rno)
    {
        log.info(" ReplyController getRead: rno={}", rno);
        ReplyDTO replyDTO = replyService.readOne(rno);
        return replyDTO;
    }

    // 댓글 수정, 로직처리
    // localhost:8080/replies/{rno:댓글번호}
    @Tag(name = "댓글 수정 로직처리",description = "댓글 수정 로직처리 RESTful get방식")
    @PutMapping(value ="/{rno}")
    public Map<String,Long> updateReply(
    @Valid @RequestBody ReplyDTO replyDTO,
    BindingResult bindingResult,
    @PathVariable("rno") Long rno) throws BindException {
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }
        log.info(" ReplyController updateReply: replyDTO={}", replyDTO);
        log.info(" ReplyController updateReply: rno={}", rno);
        replyService.update(replyDTO);
        Map<String,Long> map = Map.of("rno",rno);
        return map;
    }

    // 댓글 삭제, 로직처리
    // localhost:8080/replies/{rno:댓글번호}
    @Tag(name = "댓글 삭제 로직처리",description = "댓글 삭제 로직처리 RESTful get방식")
    @DeleteMapping(value ="/{rno}")
    public Map<String,Long> deleteReply(
            @PathVariable("rno") Long rno) throws BindException {
        replyService.delete(rno);
        Map<String,Long> map = Map.of("rno",rno);
        return map;
    }

}





