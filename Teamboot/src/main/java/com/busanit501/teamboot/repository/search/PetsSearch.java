package com.busanit501.teamboot.repository.search;

import com.busanit501.teamboot.domain.Pet;
import com.busanit501.teamboot.dto.PetListAllDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

// Querydsl 이용시, 해당 인터페이스에서, Jq
public interface PetsSearch {
    // 연습용으로, 자바 문법으로 SQL 문장 전달해보기.
    Page<Pet> search(Pageable pageable);

    //String[] types , "t", "c", "tc"
    // Pageable -> 페이징 하기 위한 재료. 현재 페이지, 페이지 보여줄 갯수, 정렬
    // Page -> 1) 페이징된 결과물 10개 2) 전체 갯수 3) 현제 페이지, 등. 정보 조회 가능.
    Page<Pet> searchAll(String[] types, String keyword, Pageable pageable);

    // 댓글 갯수를 포함한 목록,
    // 목록, board 조회를 함,
    // 단점, board , reply 쪽으로 연관관계 설정 안되어 있음.
    // 즉 조회를 못해요, 자바로 인스턴스로 ,
    // 그래서, 2개의 테이블을 연결 조인(외부조인? 댓글 null 일수도 있기때문에)
    // Page<PetListReplyCountDTO> searchWithReplyCount(String[] types, String keyword, Pageable pageable);

    // 게시글 + 댓글 갯수 + 첨부 이미지
    Page<PetListAllDTO> searchWithAll(String[] types, String keyword, Pageable pageable);

}
