package com.busanit501.teamboot.service;

import com.busanit501.teamboot.domain.Gallery;
import com.busanit501.teamboot.dto.GalleryDTO;
import com.busanit501.teamboot.dto.GalleryListAllDTO;
import com.busanit501.teamboot.dto.PageRequestDTO;
import com.busanit501.teamboot.dto.PageResponseDTO;
import com.busanit501.teamboot.repository.GalleryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class GalleryServiceImpl implements GalleryService {

    //맵퍼에게 의존 해야함.
    // 디비 작업 도구,
    private final GalleryRepository galleryRepository;

    // DTO <-> Entity class
    private final ModelMapper modelMapper;
    //dtoToEntity 메서드 이용할려고, 잠시, 한번 사용하려다가.
    // 자동으로 주입되어서, 루핑 돌고 있음. 그래서, 제거
//    private final GalleryService galleryService;

    // 게시글만 첨부된 내용.
//    @Override
//    public Long register(GalleryDTO galleryDTO) {
//        Gallery gallery = modelMapper.map(galleryDTO, Gallery.class);
//        Long galleryId = galleryRepository.save(gallery).getGalleryId();
//        return galleryId;
//    }

// 게시글 + 첨부이미지 추가된 내용.
    @Override
    public Long register(GalleryDTO galleryDTO) {
        // 교체 작업
//        Gallery gallery = modelMapper.map(galleryDTO, Gallery.class);
        Gallery gallery = dtoToEntity(galleryDTO);
        Long galleryId = galleryRepository.save(gallery).getGalleryId();
        return galleryId;
    }

    @Override
    public GalleryDTO readOne(Long galleryId) {
        Optional<Gallery> result = galleryRepository.findById(galleryId);
        Gallery gallery = result.orElseThrow();
        // 첨부 이미지를 추가한 버전으로 변경
//        GalleryDTO dto = modelMapper.map(gallery, GalleryDTO.class);
        GalleryDTO dto = entityToDto(gallery);
        return dto;
    }

    @Override
    // 첨부 이미지 추가 버전으로 수정하기.
    public void update(GalleryDTO galleryDTO) {

        Optional<Gallery> result = galleryRepository.findById(galleryDTO.getGalleryId());
        Gallery gallery = result.orElseThrow();
        gallery.changeGallery(galleryDTO.getContent());

        // 첨부 이미지들을 처리하는 로직.
        // 기존 내용 다 삭제 후, 첨부된 내용을 새로 업데이트를 하는 방식으로
        gallery.clearImages();

        // 게시글 수정시, 만약, 첨부된 이미지가 있다면, 교체 작업,
        if(galleryDTO.getFileNames() != null) {
            for (String fileName : galleryDTO.getFileNames()) {
                 String [] arr = fileName.split("_");
                 gallery.addImage(arr[0], arr[1]);
            }
        }

        galleryRepository.save(gallery);
    }

    @Override
    public void delete(Long galleryId) {
        // 댓글 존재 여부 확인 후, 있다면, 삭제하고,
        // 없다면, 기존 게시글만 삭제하면, 자동으로 첨부이미지 삭제.
        // ReplyRepository의 지원을 받아야함.
//       List<Reply> result = replyRepository.findByGalleryGalleryId(galleryId);
//       boolean checkReply = result.isEmpty() ? false : true;
//       if(checkReply) {
//           replyRepository.deleteByGallery_GalleryId(galleryId);
//       }

        // 게시글만 삭제,
        galleryRepository.deleteById(galleryId);
    }

    @Override
    public PageResponseDTO<GalleryDTO> list(PageRequestDTO pageRequestDTO) {
        String[] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        Pageable pageable = pageRequestDTO.getPageable("galleryId");

        Page<Gallery> result = galleryRepository.searchAll(types,keyword,pageable);
        // list -> PageResponseDTO 타입으로 변경 필요.

        // result.getContent() -> 페이징된 엔티티 클래스 목록
        List<GalleryDTO> dtoList = result.getContent().stream()
                .map(gallery ->modelMapper.map(gallery, GalleryDTO.class))
                .collect(Collectors.toList());


        return PageResponseDTO.<GalleryDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int) result.getTotalElements())
                .build();

    } // list


    @Override
    public PageResponseDTO<GalleryListAllDTO> listWithAll(PageRequestDTO pageRequestDTO) {


        String[] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        Pageable pageable = pageRequestDTO.getPageable("galleryId");

        // 수정1
        Page<GalleryListAllDTO> result = galleryRepository.searchWithAll(types,keyword,pageable);

        return PageResponseDTO.<GalleryListAllDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(result.getContent())
                .total((int) result.getTotalElements())
                .build();
    }
}
