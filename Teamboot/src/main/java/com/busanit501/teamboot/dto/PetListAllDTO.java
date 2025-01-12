package com.busanit501.teamboot.dto;

import com.busanit501.teamboot.domain.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PetListAllDTO {
    private Long petId;
    private String name;
    private String type;
    private LocalDate birth;
    private Gender gender;
    private Float weight;
    private String personality;
    private LocalDateTime regDate;
    //댓글 갯수 표기 하기 위한 용도.
    private Long replyCount;
    // 첨부 이미지들
    private List<PetImageDTO> petImages;
}