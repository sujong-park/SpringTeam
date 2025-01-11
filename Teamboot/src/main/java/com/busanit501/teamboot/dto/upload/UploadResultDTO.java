package com.busanit501.teamboot.dto.upload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UploadResultDTO {

    private String uuid;
    private String fileName;
    private boolean img;

    // 파일 이미지를 조회시, get 방식 조회.
    // URL 주소에서, 해당 이미지 파일명을 조회.
    public String getLink(){
        if(img){
            return "s_"+uuid+"_"+fileName;
        }
        return uuid+"_"+fileName;
    }

}
