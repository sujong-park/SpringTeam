package com.busanit501.teamboot.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude ="imageSet")
public class Pet extends BaseEntity {

    @Id // PK, 기본키,
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long petId;

    private String name;

    private String type;

    private LocalDate birth;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String personality;
    
    private Float weight;

    private String profile_picture;

    @Builder.Default
    private boolean verified = false;

    @Column(name = "is_default")
    private Boolean isDefault;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mid") // 외래 키 컬럼 이름
    private Member member;

    // 연관관계 설정,
    @OneToMany(mappedBy = "pet",
            cascade = CascadeType.ALL // 부모 테이블의 변경을 , 자식 테이블에서도 같이 적용됨.
            ,fetch = FetchType.LAZY,
            orphanRemoval = true // 고아 객체 자동 삭제 설정
    )// 필요한 시점에 조회를 함.
    // 자식테이블 : PetImage의 pet
    // 중간 테이블을 생성하지 않고, 데이터베이스 관점 처럼, 자식 테이블 입장에서 작업이 가능함.
    //N+1 , 문제 해결, 나눠서 조회하기.
    @BatchSize(size = 20)
    @Builder.Default
    private Set<PetImage> imageSet = new HashSet<>();
    public void changePet(String name, String type, String personality, Gender gender, Float weight) {
        this.name = name;
        this.type = type;
        this.personality = personality;
        this.gender = gender;
        this.weight = weight;
    }

    // 불변성 유지 위해서,
    // imageSet 관련해서, 추가및 삭제
    public void addImage(String uuid, String fileName) {
        PetImage petImage = PetImage.builder()
                .uuid(uuid)
                .fileName(fileName)
                .pet(this)
                .ord(imageSet.size())
                .build();
        // imageSet, 추가하기.
        imageSet.add(petImage);
    }
    public void clearImages() {
        // imageSet = {galleryImage1,galleryImage2,...}
        // galleryImage1 : 첨부이미지는 게시글 1번의 첨부이미지,
        // galleryImage1.gallery.getBno =>1
        // 삭제 한다는 건, 부모게시글을 참조를 안한다는 소리,
        // galleryImage1.gallery.getBno => null
        // galleryImage1 갑자기 부모가 없어져요, 즉, 고아
        // 자바 특성, 가비지 컬렉션이 알아서, 메모리 수거함.
        imageSet.forEach(galleryImage -> galleryImage.chagePet(null));
        this.imageSet.clear();
    }
    
    
    
}
