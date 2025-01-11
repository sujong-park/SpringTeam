package com.busanit501.teamboot.security.handler;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

@Log4j2
// Rest 용, 화면에 403 에러 관련해서 데이터 문자열 만 전달용.
public class Custom403Handler implements AccessDeniedHandler {
                @Override
                public void handle(HttpServletRequest request,
                                   HttpServletResponse response,
                                   AccessDeniedException accessDeniedException) throws IOException, ServletException {
                    log.info("==================Access Denied=================");
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    // JSON 요청 확인.
                    String contentType= request.getHeader("Content-Type");
                    boolean jsonRequest = contentType.startsWith("application/json");
                    log.info("jsonRequest " +jsonRequest);
                    if (jsonRequest) {
                        response.sendRedirect("/member/login?error=ACCESS_DENIED");
                    } else {
                        // 403 상태 코드 설정
                        // 방법1, 특정 페이지로 이동
                        log.info("Custom403Handler 403 에러 확인 " +jsonRequest);
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        RequestDispatcher dispatcher = request.getRequestDispatcher("/error/403");
                        dispatcher.forward(request, response);
                        //방법2, 로그인 페이지로 이동시키기
//                        response.sendRedirect("/member/login?error=ACCESS_DENIED");
        }
    }
}
