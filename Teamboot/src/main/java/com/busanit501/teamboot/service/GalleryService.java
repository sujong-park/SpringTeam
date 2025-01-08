package com.busanit501.teamboot.service;

import com.busanit501.teamboot.domain.Gallery;
import com.busanit501.teamboot.dto.*;

import java.util.List;
import java.util.stream.Collectors;

public interface GalleryService {
    Long register(GalleryDTO galleryDTO);
    GalleryDTO readOne(Long galleryId);
    void update(GalleryDTO galleryDTO);
    void delete(Long galleryId);
    PageResponseDTO<GalleryDTO> list(PageRequestDTO pageRequestDTO);

    // 게시글 + 댓글 갯수 + 첨부이미지
    PageResponseDTO<GalleryListAllDTO> listWithAll(PageRequestDTO pageRequestDTO);

    // 화면 (DTO)-> 디비(엔티티),
    // 기능: 게시글 작성,
    default Gallery dtoToEntity(GalleryDTO dto) {
        // 박스에서 꺼내서, 디비 타입(Entity) 변경.
        Gallery gallery = Gallery.builder()
                .galleryId(dto.getGalleryId())
                .content(dto.getContent())
                .writer(dto.getWriter())
                .build();

        // 첨부 이미지들이 존재한다면, 꺼내서 담기.
        if(dto.getFileNames() != null) {
            dto.getFileNames().forEach(fileName -> {
                // 파일이름 형식 = {UUID}_{파일명}
                String[] arr = fileName.split("_");
                gallery.addImage(arr[0], arr[1]);
            });

        }
        return gallery;
    }

    // 디비 -> 화면 , Entity -> dto 변환하기.
    // 기능: 조회, 상세보기,
    default GalleryDTO entityToDto(Gallery gallery) {
        GalleryDTO galleryDTO = GalleryDTO.builder()
                .galleryId(gallery.getGalleryId())
                .content(gallery.getContent())
                .writer(gallery.getWriter())
                .regDate(gallery.getRegDate())
                .modDate(gallery.getModDate())
                .build();

        // 첨부 이미지들 처리.
        List<String> fileNames =
                gallery.getImageSet().stream().sorted()
                        .map(galleryImage ->
                                galleryImage.getUuid()+"_"
                        +galleryImage.getFileName())
                        .collect(Collectors.toList());
        // 첨부 이미지들 추가하기.
        galleryDTO.setFileNames(fileNames);
        return galleryDTO;
    }

}
