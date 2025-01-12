package com.busanit501.teamboot.dto;

import com.busanit501.teamboot.domain.Gender;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class PetDTO {
    private Long petId;

    @NotBlank(message = "반려동물 이름은 필수 입력 항목입니다.")
    private String name;

    @NotBlank(message = "반려동물 종류는 필수 입력 항목입니다.")
    private String type;

    @NotNull(message = "반려동물 나이는 필수 입력 항목입니다.")
    private LocalDate birth;

    @NotNull(message = "반려동물 성별은 필수 입력 항목입니다.")
    private Gender gender;

    @NotNull(message = "반려동물 무게는 필수 입력 항목입니다.")
    @DecimalMin(value = "0.0", inclusive = true, message = "반려동물 무게는 0 이상이어야 합니다.")
    private Float weight; // Double 타입으로 변경

    @NotBlank(message = "반려동물 성격은 필수 입력 항목입니다.")
    private String personality;

    private LocalDateTime regDate;
    private LocalDateTime modDate;

    private List<String> fileNames;
}
