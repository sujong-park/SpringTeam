package com.busanit501.teamboot.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Log4j2
@RequestMapping("/hospital")
@RequiredArgsConstructor
public class HospitalController {
    @GetMapping("/list")
    public void list(@AuthenticationPrincipal UserDetails user, Model model) {
        model.addAttribute("user", user);
    }

    @GetMapping("/register")
    public void register(@AuthenticationPrincipal UserDetails user, Model model) {
        model.addAttribute("user", user);
    }
    @PostMapping("/register")
    public String registerPost(@AuthenticationPrincipal UserDetails user, Model model) {
        model.addAttribute("user", user);

        return null;
    }

    @GetMapping("/read")
    public void read(@AuthenticationPrincipal UserDetails user, Model model) {
        model.addAttribute("user", user);
    }

    @GetMapping("/update")
    public void update(@AuthenticationPrincipal UserDetails user, Model model) {
        model.addAttribute("user", user);
    }
    @PostMapping("/update")
    public String updatePost(@AuthenticationPrincipal UserDetails user, Model model) {
        model.addAttribute("user", user);
        return null;
    }

    @PostMapping("/delete")
    public String delete(@AuthenticationPrincipal UserDetails user, Model model) {
        model.addAttribute("user", user);
        return null;
    }

}
