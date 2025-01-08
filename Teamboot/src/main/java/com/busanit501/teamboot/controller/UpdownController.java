package com.busanit501.teamboot.controller;

import com.busanit501.teamboot.dto.upload.UploadFileDTO;
import com.busanit501.teamboot.dto.upload.UploadResultDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@Log4j2
public class UpdownController {
    // application.propeties, 업로드 경로 설정.
    // com.busanit501.upload.path=c:\\upload\\springTest
    // 컨트롤러에서 사용해보기.
    @Value("${com.busanit501.upload.path}")
    private String uploadPath;

    // 파일 업로드시, 
    // 같은 이름의 파일명 문제가 됨, -> UUID , 자바에서 제공해주는 랜덤 문자열을 생성하는 도구
    //  UUID(임시 생성된 문자열)+ 파일명, 작업. 
    @Tag(name = "파일 등록 post",
            description = "멀티파트 타입 형식 이용해서, post 형식으로 업로드테스트")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<UploadResultDTO> upload(UploadFileDTO uploadFileDTO) {
        log.info("UpdownController uploadFileDTO 내용 확인: "+uploadFileDTO);

        if(uploadFileDTO.getFiles() != null && uploadFileDTO.getFiles().size() > 0){

            // 서버로부터 전달 받은 이미지 파일 임시 목록 저장소
            // 추가1
            final List<UploadResultDTO> list = new ArrayList<>();

            uploadFileDTO.getFiles().forEach(multipartFile -> {
                log.info("UpdownController multipartFile.getOriginalFilename() 실제 파일 이름 확인 : "+multipartFile.getOriginalFilename());
                String originName = multipartFile.getOriginalFilename();

                String uuid = UUID.randomUUID().toString();
                log.info("UpdownController uuid 랜덤 생성 문자열 확인: "+uuid);

                // savePath -> c:\\upload\\springTest\\UUID임시생성문자열_파일명
                Path savePath = Paths.get(uploadPath,uuid+"_"+originName);

                // 추가2. 이미지 여부
                boolean image = false;


                //화면 -> 서버, 이미지 파일을 받았고,
                // 받은 이미지 파일명 중복 안되게 설정,
                // 실제 저장 경로를 , 패스 클래스 이용해서, 설정,

                // 파일 업로드 이동시, 반드시, 예외처리.
                try {
                    // 실제 파일 -> 해당 경로 -> 물리 파일 복사하는 일.
                    // 원본으로 저장,
                    multipartFile.transferTo(savePath);

                    // 컨텐츠 타입을 확인해서, 이미지라면, 썸네일 도구 이용해서,
                    // 작은 이미지로 변환 해서, 저장,
                    // 작은 이미지라는 표시, 이름 앞에 s_ 이런식으로 이름을 변경.
                    if(Files.probeContentType(savePath).startsWith("image")){

                        // 추가 4, 이미지 상태 업데이트
                        image = true;

                        // 새로운 파일을 생성. 기존 원본 이미지 -> 작은 이미지
                        File thumbFile = new File(uploadPath,"s_"+ uuid+"_"+originName);
                        // 작은 이미지 변환 도구 이용해서, 축소 작업.
                        Thumbnailator.createThumbnail(savePath.toFile(),thumbFile, 200,200);
                    }

                } catch (IOException e)
                {
                    e.printStackTrace();
                }
                // 추가 5, 이미지 임시 저장소 목록 추가 작업.
                list.add(UploadResultDTO.builder()
                                .uuid(uuid)
                                .fileName(originName)
                                .img(image)
                        .build());

            });// end forEach

            // 추가 6, 리스트 반환
            return list;
        } // end if

        return null;
    }// upload

    @Tag(name = "파일 조회 get",
            description = "멀티파트 타입 형식 이용해서, get 형식으로 이미지 읽기")
    @GetMapping(value = "/view/{fileName}")
    // Resource : 실제 이미지 자원을 말함.
    public ResponseEntity<Resource> viewFileGet(@PathVariable String fileName) {

        // Resource : 패키지, 스프링 시스템 꺼 사용하기.
        // c:\\upload\\springTest\\UUID임시생성문자열_파일명
        Resource resource = new FileSystemResource(uploadPath+File.separator+fileName);

        String resourceName = resource.getFilename();
        log.info("UpdownController resourceName : "+resourceName);

        //http 헤더 작업,
        // http 통신 전달 할 때, 전달하는 데이터의 종류를 알려줘야 함. Content-Type , 키,
        // 이미지입니다. -> MIME , type image/*, image/jpg, image/png, image/jpeg
        //푸시
        HttpHeaders headers = new HttpHeaders();
        try{// Files.probeContentType : 해당 파일명의 확장자를 확인해서, 타입을 지정하기.
            headers.add("Content-Type",
                    Files.probeContentType(resource.getFile().toPath()));
        } catch (Exception e) {
            return new ResponseEntity<>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
                    }
        // http -> 비유, 편지 봉투(헤더), 편지 내용(바디)
        return ResponseEntity.ok().headers(headers).body(resource);


    }

    @Tag(name = "파일 다운로드 get",
            description = "멀티파트 타입 형식 이용해서, get 형식으로 이미지 다운로드")
    @GetMapping(value = "/download/{filename}")
    // Resource : 실제 이미지 자원을 말함.
    public ResponseEntity<Resource> fileDownload(@PathVariable String filename) {
        try {
            // 업로드한 폴더를 가리킴.
            //c:\\upload\\springTest\\UUID임시생성문자열_파일명
            // normalize() : 해당 경로에 불필요한 요소를 제거하는 목적.
            // 불필요한 요소에보면, 상대경로, 예) ../../ -> 절대 경로 나타 내자.
            Path filePath = Paths.get(uploadPath).resolve(filename).normalize();
            log.info("UpdownController filePath : "+filePath);

            // URL 경로로 표시하는 방법.
            log.info("UpdownController filePath.toUri() : "+filePath.toUri());
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            // 파일 이름을 UTF-8로 URLEncoding
            String encodedFilename = URLEncoder.encode(resource.getFilename(), StandardCharsets.UTF_8.toString())
                    .replaceAll("\\+", "%20"); // 공백 처리

            return ResponseEntity.ok()
                    // 파일 다운로도시,
                    // 헤더 키 : Content-Disposition
                    // 값 :attachment; filename="파일명"
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFilename + "\"")
                    .body(resource);
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Tag(name = "파일 삭제 delete",
            description = "멀티파트 타입 형식 이용해서, delete 형식으로 이미지 삭제")
    @DeleteMapping(value = "/delete/{filename}")
    // Resource : 실제 이미지 자원을 말함.
    public Map<String,Boolean> fileDelete(@PathVariable String filename) {
        // 업로드 저장소 위치는 동일, 파일명 동일해서, 재사용
        Resource resource = new FileSystemResource(uploadPath+File.separator+filename);
        String resourceName = resource.getFilename();

        // 리턴 타입 Map 전달,
        Map<String,Boolean> resultMap = new HashMap<>();
        boolean deleteCheck = false;
        try {
            // 파일 삭제시, 이미지 파일일 경우, 원본 이미지와 , 썸네일 이미지 2개 있어서
            // 이미지 파일 인지 여부를 확인 후, 이미지 이면, 썸네일도 같이 제거해야함.
            String contentType = Files.probeContentType(resource.getFile().toPath());
            // 삭제 여부를 업데이트
            // 원본 파일을 제거하는 기능. (실제 물리 파일 삭제 )
            deleteCheck =resource.getFile().delete();

            if (contentType.startsWith("image")) {
                // 썸네일 파일을 생성해서, 파일 클래스로 삭제를 진행.
                // uploadPath : C:\\upload\springTest
                // File.separator : C:\\upload\springTest\test1.jpg
                File thumbFile = new File(uploadPath+File.separator,"s_"+ filename);
                // 실제 물리 파일 삭제
                thumbFile.delete();
            }
        }
        catch (Exception e) {
            log.error(e.getMessage());
        }
        resultMap.put("result", deleteCheck);
        return resultMap;
    }


}
