package com.busanit501.teamboot.service;

import com.busanit501.teamboot.domain.Board;
import com.busanit501.teamboot.dto.*;

import java.util.List;
import java.util.stream.Collectors;

public interface BoardService {
    Long register(BoardDTO boardDTO);
    BoardDTO readOne(Long bno);
    void update(BoardDTO boardDTO);
    void delete(Long bno);
    PageResponseDTO<BoardDTO> list(PageRequestDTO pageRequestDTO);
    // 게시글에 댓글 갯수 포함한 메서드
    PageResponseDTO<BoardListReplyCountDTO> listWithReplyCount(PageRequestDTO pageRequestDTO);
    // 게시글 + 댓글 갯수 + 첨부이미지
    PageResponseDTO<BoardListAllDTO> listWithAll(PageRequestDTO pageRequestDTO);

    // 화면 (DTO)-> 디비(엔티티),
    // 기능: 게시글 작성,
    default Board dtoToEntity(BoardDTO dto) {
        // 박스에서 꺼내서, 디비 타입(Entity) 변경.
        Board board = Board.builder()
                .bno(dto.getBno())
                .title(dto.getTitle())
                .content(dto.getContent())
                .writer(dto.getWriter())
                .build();

        // 첨부 이미지들이 존재한다면, 꺼내서 담기.
        if(dto.getFileNames() != null) {
            dto.getFileNames().forEach(fileName -> {
                // 파일이름 형식 = {UUID}_{파일명}
                String[] arr = fileName.split("_");
                board.addImage(arr[0], arr[1]);
            });

        }
        return board;
    }

    // 디비 -> 화면 , Entity -> dto 변환하기.
    // 기능: 조회, 상세보기,
    default BoardDTO entityToDto(Board board) {
        BoardDTO boardDTO = BoardDTO.builder()
                .bno(board.getBno())
                .title(board.getTitle())
                .content(board.getTitle())
                .writer(board.getWriter())
                .regDate(board.getRegDate())
                .modDate(board.getModDate())
                .build();

        // 첨부 이미지들 처리.
        List<String> fileNames =
                board.getImageSet().stream().sorted()
                        .map(boardImage ->
                                boardImage.getUuid()+"_"
                        +boardImage.getFileName())
                        .collect(Collectors.toList());
        // 첨부 이미지들 추가하기.
        boardDTO.setFileNames(fileNames);
        return boardDTO;
    }

}
