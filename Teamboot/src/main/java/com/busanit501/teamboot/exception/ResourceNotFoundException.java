package com.busanit501.teamboot.exception;

/**
 * 리소스를 찾을 수 없을 때 발생하는 커스텀 예외 클래스
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    // 새 생성자 추가 (원인 예외 포함)
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

