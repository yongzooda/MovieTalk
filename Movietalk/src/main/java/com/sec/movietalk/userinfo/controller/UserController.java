package com.sec.movietalk.userinfo.controller;

import com.sec.movietalk.userinfo.dto.request.SignupRequestDto;
import com.sec.movietalk.userinfo.security.CurrentUserDetails;
import com.sec.movietalk.userinfo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;


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
            return "redirect:/login"; // 이 줄이 예외 없이 실행되면 바로 리다이렉트됨
        } catch (Exception e) {
            model.addAttribute("user", dto);
            model.addAttribute("error", e.getMessage()); // 이 줄이 호출될 경우만 에러 메시지 전달됨
            return "register"; // 에러 있을 때만 다시 register.html 보여줌
        }
    }


    @GetMapping("/login")
    public String loginPage() {
        return "login"; // login.html 반환
    }


    @GetMapping("/home")
    public String home(@AuthenticationPrincipal CurrentUserDetails userDetails, Model model) {

        model.addAttribute("nickname", userDetails.getNickname());
        model.addAttribute("userId", userDetails.getUserId());
        model.addAttribute("email", userDetails.getEmail());

        return "home";
    }

    @GetMapping("/mypage")
    public String mypage(@AuthenticationPrincipal CurrentUserDetails userDetails, Model model) {

        model.addAttribute("nickname", userDetails.getNickname());
        model.addAttribute("userId", userDetails.getUserId());
        model.addAttribute("email", userDetails.getEmail());

        return "mypage/mypage";
    }


}
