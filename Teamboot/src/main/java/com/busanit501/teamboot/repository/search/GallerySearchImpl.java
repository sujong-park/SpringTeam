package com.busanit501.teamboot.repository.search;

import com.busanit501.teamboot.domain.Gallery;
import com.busanit501.teamboot.domain.QGallery;
import com.busanit501.teamboot.domain.QReply;
import com.busanit501.teamboot.dto.GalleryImageDTO;
import com.busanit501.teamboot.dto.GalleryListAllDTO;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPQLQuery;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;
import java.util.stream.Collectors;

// 반드시 이름 작성시: 인터페이스이름 + Impl
// QuerydslRepositorySupport 의무 상속,
// 만든 인터페이스 구현하기.
@Log4j2
public class GallerySearchImpl extends QuerydslRepositorySupport
        implements GallerySearch {


    // 부모 클래스 초기화, 사용하는 엔티티 클래스를 설정.
    // Gallery
    public GallerySearchImpl() {
        super(Gallery.class);
    }

    @Override
    // 자바 문법으로 , sql 문장 명령어 대체해서 전달중.
    // 1) where
    // 2) 페이징
    // 3) 제목, 내용, 조건절. 추가중.
    public Page<Gallery> search(Pageable pageable) {
        //예시,
        QGallery gallery = QGallery.gallery; // Q 도메인 객체, 엔티티 클래스(= 테이블)
        JPQLQuery<Gallery> query = from(gallery); // select * from gallery 한 결과와 비슷함.
        //select * from gallery 작성한 내용을 query 객체 형식으로 만듦.
        // 다양한 쿼리 조건을 이용할수 있음.
        // 예, where, groupby, join , pagination
        // =================================.,조건1

        // 제목, 작성자 검색 조건 추가,
        BooleanBuilder booleanBuilder = new BooleanBuilder();// "3" 제목 임시
        booleanBuilder.or(gallery.content.contains("7"));// "3" 제목 임시
        // query, 해당 조건을 적용함.
        query.where(booleanBuilder);
        // 방법2, 추가 조건으로, galleryId 가 0보다 초과 하는 조건.
        query.where(gallery.galleryId.gt(0L));

        // =================================.,조건3

        // 페이징 조건 추가하기. qeury에 페이징 조건을 추가한 상황
        this.getQuerydsl().applyPagination(pageable, query);
        // =================================.,조건2

        // 해당 조건의 데이터를 가져오기,
        List<Gallery> list = query.fetch();
        // 해당 조건에 맞는 데이터의 갯수 조회.
        long total = query.fetchCount();
        //

        return null;
    }

    @Override
    //String[] types , "t", "c", "tc"
    public Page<Gallery> searchAll(String[] types, String keyword, Pageable pageable) {
        QGallery gallery = QGallery.gallery;
        JPQLQuery<Gallery> query = from(gallery);
        // select * from gallery
        if (types != null && types.length > 0 && keyword != null) {
            // 여러 조건을 하나의 객체에 담기.
            BooleanBuilder booleanBuilder = new BooleanBuilder();
            for (String type : types) {
                switch (type) {
                    case "c":
                        booleanBuilder.or(gallery.content.contains(keyword));
                        break;
                    case "w":
                        booleanBuilder.or(gallery.writer.contains(keyword));
                        break;
                } // switch
            }// end for
            // where 조건을 적용해보기.
            query.where(booleanBuilder);
        } //end if
        // galleryId >0
        query.where(gallery.galleryId.gt(0L));
        // where 조건.

        // 페이징 조건,
        // 페이징 조건 추가하기. qeury에 페이징 조건을 추가한 상황
        this.getQuerydsl().applyPagination(pageable, query);

        // =============================================
        // 위의 조건으로,검색 조건 1) 페이징된 결과물 2) 페이징된 전체 갯수
        // 해당 조건의 데이터를 가져오기,
        List<Gallery> list = query.fetch();
        // 해당 조건에 맞는 데이터의 갯수 조회.
        long total = query.fetchCount();

        // 마지막, Page 타입으로 전달 해주기.
        Page<Gallery> result = new PageImpl<Gallery>(list, pageable, total);

        return result;
    }

    @Override
    public Page<GalleryListAllDTO> searchWithAll(String[] types, String keyword, Pageable pageable) {
        // 기본 세팅.
        QGallery gallery = QGallery.gallery;
        QReply reply = QReply.reply;
        JPQLQuery<Gallery> galleryJPQLQuery = from(gallery);// select * from gallery
        // 조인 설정 , 게시글에서 댓글에 포함된 게시글 번호와 , 게시글 번호 일치
        galleryJPQLQuery.leftJoin(reply).on(reply.board.bno.eq(gallery.galleryId));

        //기존 , 검색 조건 추가. 위의 내용 재사용.
        if (types != null && types.length > 0 && keyword != null) {
            // 여러 조건을 하나의 객체에 담기.
            BooleanBuilder booleanBuilder = new BooleanBuilder();
            for (String type : types) {
                switch (type) {
                    case "c":
                        booleanBuilder.or(gallery.content.contains(keyword));
                        break;
                    case "w":
                        booleanBuilder.or(gallery.writer.contains(keyword));
                        break;
                } // switch
            }// end for
            // where 조건을 적용해보기.
            galleryJPQLQuery.where(booleanBuilder);
        } //end if
        // bno >0
        galleryJPQLQuery.where(gallery.galleryId.gt(0L));

        galleryJPQLQuery.groupBy(gallery); // 그룹 묶기
        this.getQuerydsl().applyPagination(pageable, galleryJPQLQuery); //페이징
        // 기본 세팅.

        // 3단계, 튜플 이용해서, 데이터 형변환.
        JPQLQuery<Tuple> tupleJPQLQuery = galleryJPQLQuery.select(
                // 게시글, 댓글의 갯수를 조회한 결과,
                gallery,reply.countDistinct()
        );
        // 튜플에서, 각 데이터를 꺼내서, 형변환 작업,
        // 꺼내는 형식이 조금 다름. 맵과 비슷

        // tupleList, 튜플의 타입으로 조인된 테이블의 내용이 담겨 있음.
        List<Tuple> tupleList = tupleJPQLQuery.fetch();

        // 형변환 작업, 디비에서 조회 후 바로, DTO로 변환 작업,
        List<GalleryListAllDTO> dtoList =
                tupleList.stream().map(tuple -> {
                    // 디비에서 조회된 내용임.
                    Gallery gallery1 = (Gallery)tuple.get(gallery);
                    long replyCount = tuple.get(1, Long.class);
                    // DTO로 형변환 하는 코드,
                    GalleryListAllDTO dto = GalleryListAllDTO.builder()
                            .galleryId(gallery1.getGalleryId())
                            .content(gallery1.getContent())
                            .writer(gallery1.getWriter())
                            .regDate(gallery1.getRegDate())
                            .replyCount(replyCount)
                            .build();

                    // gallery1에 있는 첨부 이미지를 꺼내서, DTO 담기.
                    // 같이 형변환하기
                    // 첨부 이미지를 추가하는 부분, 첨부이미지_추가1,
                    // 게시글 1번에, 첨부 이미지가 3장있으면,
                    // 3장을 각각 GalleryImageDTO -> 형변환.
                    List<GalleryImageDTO> imageDTOS = gallery1.getImageSet().stream().sorted()
                            .map(galleryImage ->
                                    GalleryImageDTO.builder()
                                            .uuid(galleryImage.getUuid())
                                            .fileName(galleryImage.getFileName())
                                            .ord(galleryImage.getOrd())
                                            .build()
                            ).collect(Collectors.toList());

                    // 최종 dto, 마지막, 첨부이미지 목록들도 추가.
                    dto.setGalleryImages(imageDTOS);

                    return dto; // 댓글의 갯수 , 첨부 이미지 목록들.
                }).collect(Collectors.toList());

        // 위에 첨부
        // 페이징 된 데이터 가져오기.
        // 앞에서 사용했던, 검색 조건

        long totalCount = galleryJPQLQuery.fetchCount();
        Page<GalleryListAllDTO> page
                = new PageImpl<>(dtoList, pageable, totalCount);
        return page;
    }

    // DTO <-> Entity 형변환
    // 1단계 , 서비스, 모델맵퍼 이용해서,
    // 2단계 , 디비에서 조회 하자마자, 바로 DTO변환,Projections.bean 이용,
    // 3단계, Tuple 타입을 이용해서, 변환. 복잡도 증가, 조건 설정 변경 쉬워짐.
    //    @Override
    //    public Page<GalleryListAllDTO> searchWithAll(String[] types, String keyword, Pageable pageable) {
    //        // 기본 세팅.
    //        QGallery gallery = QGallery.gallery;
    ////        QReply reply = QReply.reply;
    //        JPQLQuery<Gallery> galleryJPQLQuery = from(gallery);// select * from gallery
    //        // 조인 설정 , 게시글에서 댓글에 포함된 게시글 번호와 , 게시글 번호 일치
    //        galleryJPQLQuery.leftJoin(reply).on(reply.gallery.galleryId.eq(gallery.galleryId));
    //
    //        //기존 , 검색 조건 추가. 위의 내용 재사용.
    //        if (types != null && types.length > 0 && keyword != null) {
    //            // 여러 조건을 하나의 객체에 담기.
    //            BooleanBuilder booleanBuilder = new BooleanBuilder();
    //            for (String type : types) {
    //                switch (type) {
    //                    case "c":
    //                        booleanBuilder.or(gallery.content.contains(keyword));
    //                        break;
    //                    case "w":
    //                        booleanBuilder.or(gallery.writer.contains(keyword));
    //                        break;
    //                } // switch
    //            }// end for
    //            // where 조건을 적용해보기.
    //            galleryJPQLQuery.where(booleanBuilder);
    //        } //end if
    //        // galleryId >0
    //        galleryJPQLQuery.where(gallery.galleryId.gt(0L));
    //
    //        galleryJPQLQuery.groupBy(gallery); // 그룹 묶기
    //        this.getQuerydsl().applyPagination(pageable, galleryJPQLQuery); //페이징
    //        // 기본 세팅.
    //
    //        // 3단계, 튜플 이용해서, 데이터 형변환.
    //        JPQLQuery<Tuple> tupleJPQLQuery = galleryJPQLQuery.select(
    //                // 게시글, 댓글의 갯수를 조회한 결과,
    //                gallery,reply.countDistinct()
    //        );
    //        // 튜플에서, 각 데이터를 꺼내서, 형변환 작업,
    //        // 꺼내는 형식이 조금 다름. 맵과 비슷
    //
    //        // tupleList, 튜플의 타입으로 조인된 테이블의 내용이 담겨 있음.
    //        List<Tuple> tupleList = tupleJPQLQuery.fetch();
    //
    //        // 형변환 작업, 디비에서 조회 후 바로, DTO로 변환 작업,
    //        List<GalleryListAllDTO> dtoList =
    //                tupleList.stream().map(tuple -> {
    //                    // 디비에서 조회된 내용임.
    //                    Gallery gallery1 = (Gallery)tuple.get(gallery);
    //                    long replyCount = tuple.get(1, Long.class);
    //                    // DTO로 형변환 하는 코드,
    //                    GalleryListAllDTO dto = GalleryListAllDTO.builder()
    //                            .galleryId(gallery1.getGalleryId())
    //                            .writer(gallery1.getWriter())
    //                            .regDate(gallery1.getRegDate())
    //                            .replyCount(replyCount)
    //                            .build();
    //
    //                    // gallery1에 있는 첨부 이미지를 꺼내서, DTO 담기.
    //                    // 같이 형변환하기
    //                    // 첨부 이미지를 추가하는 부분, 첨부이미지_추가1,
    //                    // 게시글 1번에, 첨부 이미지가 3장있으면,
    //                    // 3장을 각각 GalleryImageDTO -> 형변환.
    //                   List<GalleryImageDTO> imageDTOS = gallery1.getImageSet().stream().sorted()
    //                            .map(galleryImage ->
    //                                GalleryImageDTO.builder()
    //                                        .uuid(galleryImage.getUuid())
    //                                        .fileName(galleryImage.getFileName())
    //                                        .ord(galleryImage.getOrd())
    //                                        .build()
    //                            ).collect(Collectors.toList());
    //
    //                   // 최종 dto, 마지막, 첨부이미지 목록들도 추가.
    //                    dto.setGalleryImages(imageDTOS);
    //
    //                    return dto; // 댓글의 갯수 , 첨부 이미지 목록들.
    //                }).collect(Collectors.toList());
    //
    //        // 위에 첨부
    //        // 페이징 된 데이터 가져오기.
    //        // 앞에서 사용했던, 검색 조건
    //
    //        long totalCount = galleryJPQLQuery.fetchCount();
    //        Page<GalleryListAllDTO> page
    //                = new PageImpl<>(dtoList, pageable, totalCount);
    //        return page;
    //    }

//    @Override
//    public Page<GalleryListReplyCountDTO> searchWithAll(String[] types, String keyword, Pageable pageable) {
//        QGallery gallery = QGallery.gallery;
//        QReply reply = QReply.reply;
//        JPQLQuery<Gallery> galleryJPQLQuery = from(gallery);// select * from gallery
//        // 조인 설정 , 게시글에서 댓글에 포함된 게시글 번호와 , 게시글 번호 일치
//        galleryJPQLQuery.leftJoin(reply).on(reply.gallery.galleryId.eq(gallery.galleryId));
//        this.getQuerydsl().applyPagination(pageable, galleryJPQLQuery);
//
//        // 페이징 된 데이터 가져오기.
//        List<Gallery> galleryList = galleryJPQLQuery.fetch();
//
//        galleryList.forEach(gallery1 -> {
//            log.info("gallery1 : " + gallery1.getGalleryId());
//            log.info("gallery1 : " + gallery1.getImageSet());
//
//        });
//
//        return null;
//    }
}
