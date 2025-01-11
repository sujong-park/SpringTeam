package com.busanit501.teamboot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GalleryImageDTO {
    private String uuid;
    private String fileName;
    private int ord;
}
