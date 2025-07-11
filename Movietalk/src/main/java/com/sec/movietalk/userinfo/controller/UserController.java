package com.sec.movietalk.userinfo.controller;

import com.sec.movietalk.common.domain.user.User;
import com.sec.movietalk.common.util.UserUtil;
import com.sec.movietalk.home.dto.HomeActorDto;
import com.sec.movietalk.home.dto.HomeCommentDto;
import com.sec.movietalk.home.dto.HomeMovieDto;
import com.sec.movietalk.home.dto.HomeReviewDto;
import com.sec.movietalk.home.service.HomeService;
import com.sec.movietalk.recommendation.service.FavoriteService;
import com.sec.movietalk.review.dto.ReviewResponse;
import com.sec.movietalk.userinfo.dto.request.PasswordResetRequestDto;
import com.sec.movietalk.userinfo.dto.request.SignupRequestDto;
import com.sec.movietalk.userinfo.dto.request.WithdrawRequestDto;
import com.sec.movietalk.userinfo.dto.response.MyActorCommentResponseDto;
import com.sec.movietalk.userinfo.dto.response.MyCommentResponseDto;
import com.sec.movietalk.userinfo.dto.response.UserInfoResponseDto;
import com.sec.movietalk.userinfo.security.CurrentUserDetails;
import com.sec.movietalk.userinfo.service.MyDataService;
import com.sec.movietalk.userinfo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;


import static com.sec.movietalk.common.util.UserUtil.extractUserId;


@Controller
@RequiredArgsConstructor
public class UserController {

    private final HomeService homeService;
    private final UserService userService;
    private final MyDataService mydataService;
    private final FavoriteService favoriteService; //G1


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

        Long userId = extractUserId(principal);
        if (userId == null) {
            return "redirect:/login";
        }

        UserInfoResponseDto info = userService.getUserInfo(userId);

        model.addAttribute("nickname", info.getNickname());
        model.addAttribute("userId", info.getUserId());
        model.addAttribute("commentCnt", info.getCommentCnt());
        model.addAttribute("reviewCnt", info.getReviewCnt());

        List<HomeMovieDto> popularMovies = homeService.getTop4MoviesByViews();
        model.addAttribute("popularMovies", popularMovies);

        List<HomeReviewDto> topReviews = homeService.getTop3Reviews();
        model.addAttribute("topReviews", topReviews);

        List<HomeCommentDto> topComments = homeService.getTop3Comments();
        model.addAttribute("topComments", topComments);

        List<HomeActorDto> topActors = homeService.getTop4ActorsByCommentCount();
        model.addAttribute("topActors", topActors);

        return "home";
    }

    @GetMapping("/mypage")
    public String mypage(Authentication authentication, Model model) {

        String nickname = null;
        String email = null;
        Long userId = null;

        if (authentication == null) {

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
            nickname = oAuth2User.getAttribute("nickname");
            email = oAuth2User.getAttribute("email");
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

        Long userId = extractUserId(principal);

        if (userId == null) {
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


    @GetMapping("/mypage/reviews")
    public String myReviews(@AuthenticationPrincipal Object principal, Model model) {

        Long userId = extractUserId(principal);

        if (userId == null) {
            return "redirect:/login";
        }

        List<ReviewResponse> myReviews = mydataService.getReviewsByUserId(userId);
        model.addAttribute("reviewList", myReviews);
        return "mypage/my_review";
    }

    @GetMapping("/mypage/comments")
    public String myComments(@AuthenticationPrincipal Object principal, Model model) {

        Long userId = extractUserId(principal);

        if (userId == null) {
            return "redirect:/login";
        }

        List<MyCommentResponseDto> myComments = mydataService.getCommentsByUserId(userId);
        model.addAttribute("commentList", myComments);
        return "mypage/my_comment";

    }

    @GetMapping("/mypage/actorcomments")
    public String myActorComments(@AuthenticationPrincipal Object principal, Model model) {

        Long userId = extractUserId(principal);

        if (userId == null) {
            return "redirect:/login";
        }

        List<MyActorCommentResponseDto> myActorComments = mydataService.getActorCommentsByUserId(userId);
        model.addAttribute("actorCommentList", myActorComments);
        return "mypage/my_actorcomment";

    }

    @GetMapping("/mypage/withdraw")
    public String showWithdrawForm(@AuthenticationPrincipal Object principal, Model model) {
        Long userId = UserUtil.extractUserId(principal);
        if (userId == null) return "redirect:/login";

        model.addAttribute("withdraw", new WithdrawRequestDto());
        boolean isSocial = userService.isSocialUser(userId);
        model.addAttribute("isSocial", isSocial);

        return "mypage/withdraw";
    }

    @PostMapping("/mypage/withdraw")
    public String withdraw(@ModelAttribute WithdrawRequestDto dto,
                           @AuthenticationPrincipal Object principal,
                           Model model) {
        Long userId = UserUtil.extractUserId(principal);
        if (userId == null) return "redirect:/login";

        try {
            userService.withdrawUser(userId, dto);
            return "redirect:/logout";
        } catch (Exception e) {
            model.addAttribute("withdraw", dto);
            model.addAttribute("error", e.getMessage());
            boolean isSocial = userService.isSocialUser(userId);
            model.addAttribute("isSocial", isSocial);
            return "mypage/withdraw";
        }
    }


    // ---------------------- G1 영화 즐겨찾기 ----------------------
    /** 즐겨찾기 페이지 */
    @GetMapping("/mypage/favorites")
    public String favorites(@AuthenticationPrincipal Object principal, Model model) {
        Long userId = extractUserId(principal);
        if (userId == null) return "redirect:/login";
        model.addAttribute("favoriteMovies", favoriteService.getFavorites(userId));
        return "mypage/my_favorite";
    }

    /** 즐겨찾기 추가 (AJAX) */
    @PostMapping("/api/favorites/{movieId}")
    @ResponseBody
    public void addFavorite(@AuthenticationPrincipal Object principal,
                            @PathVariable Integer movieId) {
        favoriteService.addFavorite(extractUserId(principal), movieId);
    }

    /** 즐겨찾기 삭제 (AJAX) */
    @DeleteMapping("/api/favorites/{movieId}")
    @ResponseBody
    public void deleteFavorite(@AuthenticationPrincipal Object principal,
                               @PathVariable Integer movieId) {
        favoriteService.removeFavorite(extractUserId(principal), movieId);
    }



}








