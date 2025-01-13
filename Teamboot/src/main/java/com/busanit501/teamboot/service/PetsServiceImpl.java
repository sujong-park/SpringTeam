package com.busanit501.teamboot.service;

import com.busanit501.teamboot.domain.Pet;
import com.busanit501.teamboot.dto.PetDTO;
import com.busanit501.teamboot.dto.PetListAllDTO;
import com.busanit501.teamboot.dto.PageRequestDTO;
import com.busanit501.teamboot.dto.PageResponseDTO;
import com.busanit501.teamboot.repository.PetsRepository;
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
public class PetsServiceImpl implements PetsService {

    //맵퍼에게 의존 해야함.
    // 디비 작업 도구,
    private final PetsRepository petsRepository;

    // DTO <-> Entity class
    private final ModelMapper modelMapper;
    //dtoToEntity 메서드 이용할려고, 잠시, 한번 사용하려다가.
    // 자동으로 주입되어서, 루핑 돌고 있음. 그래서, 제거
//    private final PetService petService;

    // 게시글만 첨부된 내용.
//    @Override
//    public Long register(PetDTO petDTO) {
//        Pet pet = modelMapper.map(petDTO, Pet.class);
//        Long petId = petsRepository.save(pet).getPetId();
//        return petId;
//    }

// 게시글 + 첨부이미지 추가된 내용.
    @Override
    public Long register(PetDTO petDTO) {
        // 교체 작업
//        Pet pet = modelMapper.map(petDTO, Pet.class);
        Pet pet = dtoToEntity(petDTO);
        Long petId = petsRepository.save(pet).getPetId();
        return petId;
    }

    @Override
    public PetDTO readOne(Long petId) {
        Optional<Pet> result = petsRepository.findById(petId);
        Pet pet = result.orElseThrow();
        // 첨부 이미지를 추가한 버전으로 변경
//        PetDTO dto = modelMapper.map(pet, PetDTO.class);
        PetDTO dto = entityToDto(pet);
        return dto;
    }

    @Override
    // 첨부 이미지 추가 버전으로 수정하기.
    public void update(PetDTO petDTO) {

        Optional<Pet> result = petsRepository.findById(petDTO.getPetId());
        Pet pet = result.orElseThrow();
        pet.changePet(petDTO.getName(), petDTO.getType(), petDTO.getPersonality(), petDTO.getGender(), petDTO.getWeight());
        

        // 첨부 이미지들을 처리하는 로직.
        // 기존 내용 다 삭제 후, 첨부된 내용을 새로 업데이트를 하는 방식으로
        pet.clearImages();

        // 게시글 수정시, 만약, 첨부된 이미지가 있다면, 교체 작업,
        if(petDTO.getFileNames() != null) {
            for (String fileName : petDTO.getFileNames()) {
                 String [] arr = fileName.split("_");
                 pet.addImage(arr[0], arr[1]);
            }
        }

        petsRepository.save(pet);
    }

    @Override
    public void delete(Long petId) {
        // 댓글 존재 여부 확인 후, 있다면, 삭제하고,
        // 없다면, 기존 게시글만 삭제하면, 자동으로 첨부이미지 삭제.
        // ReplyRepository의 지원을 받아야함.
//       List<Reply> result = replyRepository.findByPetPetId(petId);
//       boolean checkReply = result.isEmpty() ? false : true;
//       if(checkReply) {
//           replyRepository.deleteByPet_PetId(petId);
//       }

        // 게시글만 삭제,
        petsRepository.deleteById(petId);
    }

    @Override
    public PageResponseDTO<PetDTO> list(PageRequestDTO pageRequestDTO) {
        String[] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        Pageable pageable = pageRequestDTO.getPageable("petId");

        Page<Pet> result = petsRepository.searchAll(types,keyword,pageable);
        // list -> PageResponseDTO 타입으로 변경 필요.

        // result.getContent() -> 페이징된 엔티티 클래스 목록
        List<PetDTO> dtoList = result.getContent().stream()
                .map(pet ->modelMapper.map(pet, PetDTO.class))
                .collect(Collectors.toList());


        return PageResponseDTO.<PetDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int) result.getTotalElements())
                .build();

    } // list


    @Override
    public PageResponseDTO<PetListAllDTO> listWithAll(PageRequestDTO pageRequestDTO) {


        String[] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        Pageable pageable = pageRequestDTO.getPageable("petId");

        // 수정1
        Page<PetListAllDTO> result = petsRepository.searchWithAll(types,keyword,pageable);

        return PageResponseDTO.<PetListAllDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(result.getContent())
                .total((int) result.getTotalElements())
                .build();
    }
}
