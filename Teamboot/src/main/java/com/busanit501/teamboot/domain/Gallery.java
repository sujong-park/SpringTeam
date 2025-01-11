package com.busanit501.teamboot.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.util.HashSet;
import java.util.Set;

@Entity // JPA 이용해서, 엔티티 클래스 , 데이터베이스 테이블 만들기 놀이.
@Getter // 비지니스 모델, 디비에는 불변성 유지, 수정안함., 안에 메서드 이용해서 멤버 교체식으로함.
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude ="imageSet")//로그를 해당 인스턴스 찍을 때, 해당 멤버를 쉽게 확인 가능.
//@ToString
public class Gallery extends BaseEntity { // 전역으로 만든, 베이스 엔티티 클래스  적용.

    //2 번째 작업, 제약조건 넣기, 각 멤버는 각 디비의 컬럼과 동일함,
    // 그래서,각각에 제약조건 설정하기.

    @Id // PK, 기본키,
    // Oracle, 시퀸스 객체, 이용시, SEQUENCE 설정, 추가 설정. 필요함.
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 마리아디비,
    private Long galleryId;

    @Column(length = 2000, nullable = false)// 길이 500자,  NotNull=nn
    private String content;

    @Column(length = 50, nullable = false)// 길이 500자,  NotNull=nn
    private String writer;

    // 연관관계 설정,
    @OneToMany(mappedBy = "gallery",
            cascade = CascadeType.ALL // 부모 테이블의 변경을 , 자식 테이블에서도 같이 적용됨.
            ,fetch = FetchType.LAZY,
            orphanRemoval = true // 고아 객체 자동 삭제 설정
    )// 필요한 시점에 조회를 함.
    // 자식테이블 : GalleryImage의 gallery
    // 중간 테이블을 생성하지 않고, 데이터베이스 관점 처럼, 자식 테이블 입장에서 작업이 가능함.
    //N+1 , 문제 해결, 나눠서 조회하기.
    @BatchSize(size = 20)
    @Builder.Default
    private Set<GalleryImage> imageSet = new HashSet<>();


    // 모든 테이블에 공통으로 들어갈수 있는, 등록시간, 수정시간, 등,
    // 베이스 엔티티에서 작업 할 예정.

    // 해당 엔티티 클래스는, 각 인스턴스가, 해당 디비의 각 행 데이터와 동일함.
    // 그래서, 바로 수정 불가하고, 조회만 하게하고,
    // 만약, 수정이 필요하다면, 메서드로 안전하게 내용만 변경함.
    public void changeGallery(String content) {
        this.content = content;
    }

    // 불변성 유지 위해서,
    // imageSet 관련해서, 추가및 삭제
    public void addImage(String uuid, String fileName) {
        GalleryImage galleryImage = GalleryImage.builder()
                .uuid(uuid)
                .fileName(fileName)
                .gallery(this)
                .ord(imageSet.size())
                .build();
        // imageSet, 추가하기.
        imageSet.add(galleryImage);
    }

    public void clearImages() {
        // imageSet = {galleryImage1,galleryImage2,...}
        // galleryImage1 : 첨부이미지는 게시글 1번의 첨부이미지,
        // galleryImage1.gallery.getBno =>1
        // 삭제 한다는 건, 부모게시글을 참조를 안한다는 소리,
        // galleryImage1.gallery.getBno => null
        // galleryImage1 갑자기 부모가 없어져요, 즉, 고아
        // 자바 특성, 가비지 컬렉션이 알아서, 메모리 수거함.
        imageSet.forEach(galleryImage -> galleryImage.chageGallery(null));
        this.imageSet.clear();
    }
}
