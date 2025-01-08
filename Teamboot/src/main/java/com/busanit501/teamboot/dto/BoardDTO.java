package com.busanit501.teamboot.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardDTO {
    private  Long bno;
    @NotEmpty
    @Size(min = 3, max = 100)
    private  String title;

    @NotEmpty
    private  String content;

    @NotEmpty
    private  String writer;
    private LocalDateTime regDate;
    private LocalDateTime modDate;

    // 첨부 이미지 파일들 (파일 이름들)
    // uuid_파일명
    //예시)
    // s_b1253cba-fb64-4699-8330-5c735a288501_base-공통레이아웃-web.png
    private List<String> fileNames;
}
