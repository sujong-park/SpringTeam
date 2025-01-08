package com.busanit501.teamboot.controller;

//@Controller
//@Log4j2
//public class CustomErrorController implements ErrorController {
//    @RequestMapping("/error")
//    public String handleError(HttpServletRequest request, Model model) {
//        log.info("CustomErrorController /error : ");
//        //상태 코드 가져오기.

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

////        int statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
//       int statusCode = 403;
//        log.info("CustomErrorController /error : statusCode " + statusCode);
//        Integer statusCode2 = (Integer) request.getAttribute("javax.servlet.error.status_code");
//        if (statusCode2 != null) {
//            System.out.println("Error Code: statusCode2" + statusCode2);
//        } else {
//            System.out.println("Status code is null statusCode2 : " + statusCode2);
//        }
//        if (statusCode == 403) {
//            log.info("403 페이지 이동 확인 : ");
//            log.info("403 페이지 이동 확인 statusCode : " + statusCode);
//            return "/error/403";
//        } else if (statusCode == 404) {
//            return "/error/404";
//        }
//        return "/error/500";
//    }
//}

//2
@Controller
@Log4j2
public class CustomErrorController {

    @PostMapping("/error/403")
    public String accessDeniedPage() {
        log.info("CustomErrorController /error : ");
        return "error/403"; // 403.html 템플릿 반환
    }
}
