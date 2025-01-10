package com.busanit501.teamboot.gallery;

import com.busanit501.teamboot.dto.*;
import com.busanit501.teamboot.service.GalleryService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.IntStream;

@Log4j2
@SpringBootTest
public class GalleryServiceTests {
    @Autowired
    private GalleryService galleryService;

    @Test
    public void testRegisterGallery() {
        IntStream.range(1,20).forEach(i -> {
            GalleryDTO galleryDTO = GalleryDTO.builder()
                    .content("GPT(Generative Pre-trained Transformer)는 OpenAI에서 개발한 인공지능 언어 모델로, 자연어를 \n" +
                            "이해하고 생성하는 데 특화되어 있습니다. 다양한 언어를 학습해 사람처럼 텍스트를 작성하거나 질문에 답할 수 \n" +
                            "있습니다.\n" +
                            "\n" +
                            "GPT는 개인의 생산성을 높이고, 창의적인 작업을 지원하며, \n" +
                            "문제 해결에 도움을 주는 도구로 점점 더 많은 분야에서 \n" +
                            "사용되고 있습니다. \uD83D\uDE0A")
                    .writer("test")
                    .regDate(LocalDateTime.now())
                    .modDate(LocalDateTime.now())
                    .build();

            Long galleryId = galleryService.register(galleryDTO);
            log.info("입력한 게시글 번호: " + galleryId.toString());
        });
    }

    @Test
    public void testRegisterGalleryOne() {
        // 더미 데이터 필요, 임시 DTO 생성.
        GalleryDTO galleryDTO = GalleryDTO.builder()
                .content("aaaaaaaaaaaaaa")
                .writer("test")
                .regDate(LocalDateTime.now())
                .modDate(LocalDateTime.now())
                .build();

        Long galleryId = galleryService.register(galleryDTO);
        log.info("입력한 게시글 번호: " + galleryId.toString());
    }

    @Test
    public void testSelectOneGallery() {
        // 더미 데이터 필요, 임시 DTO 생성.
        Long galleryId = 1L;
        GalleryDTO galleryDTO= galleryService.readOne(galleryId);
        log.info("testSelectOneGallery , 하나 조회 galleryDTO: " + galleryDTO.toString());
    }

    @Test
    public void testUpdateGallery() {
        // 수정할 더미 데이터 필요,
        GalleryDTO galleryDTO = GalleryDTO.builder()
                .galleryId(2L)
                .content("수정된 내용")
                .build();
        galleryService.update(galleryDTO);

    }

    @Test
    public void testDeleteGallery() {
        galleryService.delete(3L);
    }

    // ============ //

    @Test
    public void testSelectAllGallery() {
        // 검색할 더미 데이터
        // 준비물 1) PageRequestDTO, 키워드, 페이지, 사이즈 정보가 다 있음.
        PageRequestDTO pageRequestDTO =
                PageRequestDTO.builder()
                        .page(1)
                        .type("tcw")
                        .keyword("샘플")
                        .size(10)
                        .build();

        PageResponseDTO<GalleryDTO> list = galleryService.list(pageRequestDTO);
        log.info("list: " + list.toString());
    }

    @Test
    public void testRegisterGalleryWithImage() {
        // 더미 게시글
        GalleryDTO galleryDTO = GalleryDTO.builder()
                .content("첨부 이미지 추가 더미 게시글 내용")
                .writer("이상용첨부이미지작업중")
                .build();

//         더미 파일 이름들
        galleryDTO.setFileNames(
                Arrays.asList(
                        UUID.randomUUID()+"_aa.png",
                        UUID.randomUUID()+"_bb.png",
                        UUID.randomUUID()+"_cc.png"
                )
        );
        Long galleryId = galleryService.register(galleryDTO);
        log.info("galleryId: " + galleryId);


    }

    // 상세보기, 조회 기능 단위 테스트
    @Test
    public void testReadWithImage() {

        GalleryDTO galleryDTO = galleryService.readOne(2L);
        log.info("testReadWithImage, 하나 조회 galleryDTO : " + galleryDTO);
        for(String fileImage : galleryDTO.getFileNames()){
            log.info("각 이미지 파일명만 조회 : " + fileImage);
        }

    }

    // 수정, 첨부 이미지를 수정 할 경우,

    @Test
    public void testUpdateWithImages() {
        // 변경시, 변경할 더미 데이터, 임시, 601L
// 화면에서 넘어온 더미 데이터 만들기. DTO 타입.
        GalleryDTO galleryDTO = GalleryDTO.builder()
                .galleryId(2L)
                .content("내용 : 수정버전")
                .build();

        // 더미 데이터에 첨부 이미지 파일 추가.
        // 경우의수,
        // 기존의 첨부 이미지들을 모두 지우고, 새로운 첨부 이미지를 추가.
        // 1) 기존 첨부이미지 3장, 모두 교체할 경우.
        // 예시)1.jpg,2.jpg,3.jpg -> 4.jpg, 5.jpg

        // 2) 기존 첨부이미지 3장, 2장 삭제, 1장 교체할 경우.
        // 예시)1.jpg(유지),2.jpg(삭제),3.jpg(삭제)
        //  4.jpg(추가), 5.jpg(추가) -> 1.jpg(유지), 4.jpg(추가), 5.jpg(추가)
        galleryDTO.setFileNames(
                Arrays.asList(
                        UUID.randomUUID()+"_sampleImage.png",
                        UUID.randomUUID()+"_sampleImage2.png"
                )
        );

        //디비에서 조회하기.
        galleryService.update(galleryDTO);
    }

    // 삭제 테스트 1) 댓글이 있는 경우, 2) 댓글 없는 경우
    @Test
    public void testDeleteGalleryReplyWithImage() {
        Long galleryId = 2L;
        galleryService.delete(galleryId);
    }

    // 모두조회, 게시글 + 댓글갯수 + 첨부 이미지들
    @Test
    @Transactional
    public void testSelectAllGalleryWithReplyCountAndImage() {
        // 검색할 더미 데이터
        // 준비물 1) PageRequestDTO, 키워드, 페이지, 사이즈 정보가 다 있음.
        PageRequestDTO pageRequestDTO =
                PageRequestDTO.builder()
                        .page(1)
                        .type("tcw")
                        .keyword("ㅇㅇ")
                        .size(10)
                        .build();

        PageResponseDTO<GalleryListAllDTO> list = galleryService.listWithAll(pageRequestDTO);
        log.info("list: " + list.toString());
    }

}
