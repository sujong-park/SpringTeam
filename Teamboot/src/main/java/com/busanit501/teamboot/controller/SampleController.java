package com.busanit501.teamboot.controller;

import com.busanit501.teamboot.dto.SampleDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller // 1)화면 제공 2) 데이터 제공
@Log4j2
public class SampleController {

    @GetMapping("/hello")
    // 레거시에서 앞단 화면을 jsp 사용했고,
    // 부트에서 앞단 화면을 타임리프 사용. 확장자, .html 동일함.
    public void hello(Model model) {
        // 레거시, 뷰 설정, xml 등록,
        // WEB-INF/Views/todo prefix
        // .jsp , suffix
        // 기본 : templates/hello.html

        model.addAttribute("msg", "hello world");
        model.addAttribute("msg2", "어제 부터 부트 작업");
    }

    @GetMapping("/index")
    public void index(Model model) {
        List<String> list = Arrays.asList("a", "b", "c");
        model.addAttribute("list", list);
    }

    @GetMapping("/ex/ex1")
    public void ex1(Model model) {
        List<String> list = Arrays.asList("a", "b", "c");
        model.addAttribute("list", list);
    }

    @GetMapping("/ex/ex2")
    public void ex2(Model model) {
        List<String> strList = IntStream.range(1, 10)
                .mapToObj(i -> "임시 데이터 " +i)
                .collect(Collectors.toList());
        model.addAttribute("strList", strList);

        Map<String,String> map = new HashMap<>();
        map.put("a", "aaa");
        map.put("b", "bbb");
        model.addAttribute("map", map);

        SampleDTO sampleDTO = SampleDTO.builder()
                .p1("테스트 p1")
                .p2("테스트 p2")
                .p3("테스트 p3")
                .p4("테스트 p4")
                .build();
        model.addAttribute("sampleDTO", sampleDTO);

    }

    @GetMapping("/ex/ex3")
    public void ex3(Model model) {
        List<String> list = Arrays.asList("a", "b", "c");
        model.addAttribute("list", list);
    }

}
