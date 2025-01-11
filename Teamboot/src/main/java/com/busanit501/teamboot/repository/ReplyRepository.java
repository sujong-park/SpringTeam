package com.busanit501.teamboot.repository;

import com.busanit501.teamboot.domain.Reply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
    // 기본적인 crud , 쿼리 스트링으로 가능함.

    // 댓글 목록 조회 해보기.
    @Query("select r from Reply r where r.board.bno = :bno")
    Page<Reply> listOfBoard(Long bno, Pageable pageable);

    //게시글은, 외래키, 댓글.
    // 댓글이 삭제가 되어야, 부모 게시글을 삭제가 가능함.
    // 부모 게시글 1, 자식 댓글 1,  자식, 첨부 이미지 2
    // 이런 경우, 삭제시, 어떻게 동작 해야하나요?
    // 댓글 삭제 후, 부모 게시글 삭제,  첨부 이미지 (영속성 전이) 같이 삭제.
    void deleteByBoard_Bno(Long bno);

    // 부모 게시글에 대한, 댓글의 목록 조회.
    List<Reply> findByBoardBno(Long bno);

}
