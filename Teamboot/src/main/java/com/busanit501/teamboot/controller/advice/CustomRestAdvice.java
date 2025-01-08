package com.busanit501.teamboot.controller.advice;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Log4j2
public class CustomRestAdvice {
    @ExceptionHandler(BindException.class)// 예외 처리할 종류를 알려주기
    @ResponseStatus(HttpStatus.EXPECTATION_FAILED) // 응답코드, 예외 실패라서 안됐습니다. 메세지 코드 전달.
    public ResponseEntity<Map<String,String>> handleException(BindException e){

        log.error("CustomRestAdvice hadle exception ", e);
        log.error(e);

        Map<String,String> errorMap = new HashMap<>();
        if(e.hasErrors()){
            BindingResult bindingResult = e.getBindingResult();
            bindingResult.getFieldErrors()
                    .forEach(fieldError -> {
                        errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
                    });
        }
        return ResponseEntity.badRequest().body(errorMap);
    }
}
