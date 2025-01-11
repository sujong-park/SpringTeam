package com.busanit501.teamboot.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "gallery")
// 단위 테스트시, 해당 보드를 조회시, 또 디비 접근하는 세션이 필요함.
// 그래서, 조회를 미리 못하게함.
// 단위테스트 할 때,  @Transanction 설정해도,
// 결론, 단위 테스트시, 2개의 테이블에 접근시, 세션 문제가 발생 안함.
public class GalleryImage implements Comparable<GalleryImage>{
    @Id
    private String uuid; // 랜덤함 임시 문자열, 파일 이름 중복 방지용
    private String fileName;
    private int ord; // 첨부 이미지의 크기로 순서 정하기.
    // 첨부 파일이 등록시,
    // 순차적으로, 1번 이미지가 첨부시, 리스트크기 : 1 , ord : 0
    // 순차적으로, 2번 이미지가 첨부시, 리스트크기 : 2 , ord : 1

    @ManyToOne
    private Gallery gallery;


    @Override
    public int compareTo(GalleryImage other) {
        // this.ord - other.ord > 0 , 오름차순,
        // other.ord - this.ord , 내림차순.
        return this.ord - other.ord;
    }

    //엔티티 불변성 유지 위해서, 값의 변경 , 메서드를 만들어서 교체 방식으로 함.
    public void chageGallery(Gallery gallery) {
        this.gallery = gallery;
    }
}
