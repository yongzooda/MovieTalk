package com.sec.movietalk.userinfo.controller;

import com.sec.movietalk.userinfo.dto.request.PasswordResetRequestDto;
import com.sec.movietalk.userinfo.dto.request.SignupRequestDto;
import com.sec.movietalk.userinfo.dto.response.UserInfoResponseDto;
import com.sec.movietalk.userinfo.security.CurrentUserDetails;
import com.sec.movietalk.userinfo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
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
    public String showRegisterForm(@AuthenticationPrincipal CurrentUserDetails userDetails, Model model) {
        if (userDetails != null) {
            return "redirect:/home";
        }
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
    public String loginPage(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            // 이미 로그인된 경우(세션 유지 중) 메인화면 등으로 리다이렉트
            return "redirect:/home";
        }
        return "login";
    }

    @GetMapping("/")
    public String startPage(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/home";
        }
        return "login";
    }


    @GetMapping("/home")
    public String home(@AuthenticationPrincipal Object principal, Model model) {

        String nickname = "";
        String email = "";
        Long userId = null;


        if (principal instanceof CurrentUserDetails userDetails) {
            nickname = userDetails.getNickname();
            email = userDetails.getEmail();
            userId = userDetails.getUserId();
        }

        else if (principal instanceof org.springframework.security.oauth2.core.user.OAuth2User oAuth2User) {
            nickname = (String) oAuth2User.getAttribute("nickname");
            email = (String) oAuth2User.getAttribute("email");
            Object idObj = oAuth2User.getAttribute("userId");
            // userId가 Long 타입인지 체크
            if (idObj instanceof Long) userId = (Long) idObj;
            else if (idObj instanceof Integer) userId = ((Integer) idObj).longValue();
        }

        model.addAttribute("nickname", nickname);
        model.addAttribute("userId", userId);
        model.addAttribute("email", email);

        return "home";
    }

    @GetMapping("/mypage")
    public String mypage(Authentication authentication, Model model) {
        String nickname = null;
        String email = null;
        Long userId = null;

        if (authentication == null) {
            // 비로그인 접근 시 리다이렉트 등
            return "redirect:/login";
        }

        Object principal = authentication.getPrincipal();

        // 1. 일반 로그인
        if (principal instanceof CurrentUserDetails userDetails) {

            nickname = userDetails.getNickname();
            email = userDetails.getEmail();
            userId = userDetails.getUserId();
        }
        // 2. 소셜 로그인
        else if (principal instanceof org.springframework.security.oauth2.core.user.OAuth2User oAuth2User) {
            nickname = (String) oAuth2User.getAttribute("nickname");
            email = (String) oAuth2User.getAttribute("email");
            Object idObj = oAuth2User.getAttribute("userId");
            if (idObj instanceof Long) userId = (Long) idObj;
            else if (idObj instanceof Integer) userId = ((Integer) idObj).longValue();
            // 기타 타입 체크
        }

        // 모델에 값 넣기
        model.addAttribute("nickname", nickname);
        model.addAttribute("email", email);
        model.addAttribute("userId", userId);

        return "mypage/mypage";
    }


    @GetMapping("/findpassword")
    public String showFindPasswordForm(@AuthenticationPrincipal CurrentUserDetails userDetails, Model model) {
        if (userDetails != null) {
            return "redirect:/home";
        }
        model.addAttribute("user", new PasswordResetRequestDto());
        return "findpw";
    }


    @PostMapping("/findpassword")
    public String register(@ModelAttribute PasswordResetRequestDto dto, Model model) {
        try {
            userService.resetPassword(dto);
            return "redirect:/login"; // 이 줄이 예외 없이 실행되면 바로 리다이렉트됨
        } catch (Exception e) {
            model.addAttribute("user", dto);
            model.addAttribute("error", e.getMessage()); // 이 줄이 호출될 경우만 에러 메시지 전달됨
            return "findpw"; // 에러 있을 때만 다시 register.html 보여줌
        }
    }

    @GetMapping("/mypage/my_info")
    public String myInfo(Authentication authentication, Model model) {

        Object principal = authentication.getPrincipal();

        Long userId = null;
        String nickname = null;
        String email = null;

        if (principal instanceof CurrentUserDetails currentUser) {
            userId = currentUser.getUserId();
            nickname = currentUser.getNickname();
            email = currentUser.getEmail();
        } else if (principal instanceof OAuth2User oAuth2User) {
            userId = Long.valueOf(oAuth2User.getAttribute("userId").toString());
            nickname = oAuth2User.getAttribute("nickname");
            email = oAuth2User.getAttribute("email");
        } else {
            // 로그인 정보가 없거나 알 수 없는 타입일 때 처리
            return "redirect:/login";
        }

        UserInfoResponseDto info = userService.getUserInfo(userId);

        model.addAttribute("nickname", info.getNickname());
        model.addAttribute("userId", info.getUserId());
        model.addAttribute("email", info.getEmail());
        model.addAttribute("commentCnt", info.getCommentCnt());
        model.addAttribute("reviewCnt", info.getReviewCnt());

        return "mypage/my_info";
    }






}
