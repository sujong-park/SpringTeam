package com.busanit501.teamboot.service;

import com.busanit501.teamboot.domain.Pet;
import com.busanit501.teamboot.dto.PetDTO;
import com.busanit501.teamboot.dto.PetListAllDTO;
import com.busanit501.teamboot.dto.PageRequestDTO;
import com.busanit501.teamboot.dto.PageResponseDTO;

import java.util.List;
import java.util.stream.Collectors;

public interface PetsService {
    Long register(PetDTO petDTO);
    PetDTO readOne(Long petId);
    void update(PetDTO petDTO);
    void delete(Long petId);
    PageResponseDTO<PetDTO> list(PageRequestDTO pageRequestDTO);

    // 게시글 + 댓글 갯수 + 첨부이미지
    PageResponseDTO<PetListAllDTO> listWithAll(PageRequestDTO pageRequestDTO);

    // 화면 (DTO)-> 디비(엔티티),
    // 기능: 게시글 작성,
    default Pet dtoToEntity(PetDTO dto) {
        // 박스에서 꺼내서, 디비 타입(Entity) 변경.
        Pet pet = Pet.builder()
                .petId(dto.getPetId())
                .name(dto.getName())
                .type(dto.getType())
                .birth(dto.getBirth())
                .gender(dto.getGender())
                .weight(dto.getWeight())
                .personality(dto.getPersonality())
                .build();

        // 첨부 이미지들이 존재한다면, 꺼내서 담기.
        if(dto.getFileNames() != null) {
            dto.getFileNames().forEach(fileName -> {
                // 파일이름 형식 = {UUID}_{파일명}
                String[] arr = fileName.split("_");
                pet.addImage(arr[0], arr[1]);
            });

        }
        return pet;
    }

    // 디비 -> 화면 , Entity -> dto 변환하기.
    // 기능: 조회, 상세보기,
    default PetDTO entityToDto(Pet pet) {
        PetDTO petDTO = PetDTO.builder()
                .petId(pet.getPetId())
                .name(pet.getName())
                .type(pet.getType())
                .birth(pet.getBirth())
                .gender(pet.getGender())
                .weight(pet.getWeight())
                .personality(pet.getPersonality())
                .regDate(pet.getRegDate())
                .modDate(pet.getModDate())
                .build();

        // 첨부 이미지들 처리.
        List<String> fileNames =
                pet.getImageSet().stream().sorted()
                        .map(petImage ->
                                petImage.getUuid()+"_"
                        +petImage.getFileName())
                        .collect(Collectors.toList());
        // 첨부 이미지들 추가하기.
        petDTO.setFileNames(fileNames);
        return petDTO;
    }

}
