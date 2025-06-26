package com.sec.movietalk.userinfo.controller;

import com.sec.movietalk.userinfo.dto.request.SignupRequestDto;
import com.sec.movietalk.userinfo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import com.sec.movietalk.common.domain.user.User;


@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new SignupRequestDto());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute SignupRequestDto dto, Model model) {
        try {
            userService.registerUser(dto);
            return "register";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }
}
