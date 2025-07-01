package com.sec.movietalk.review.controller;

import com.sec.movietalk.common.domain.user.User;
import com.sec.movietalk.review.dto.ReviewReactionRequest;
import com.sec.movietalk.review.dto.ReviewReactionResponse;
import com.sec.movietalk.review.service.ReviewService;
import com.sec.movietalk.userinfo.security.CurrentUserDetails;
import com.sec.movietalk.userinfo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final UserService userService; // ★★★★★ 추가!

    // 좋아요/싫어요 토글 (Ajax)
    @PostMapping("/{reviewId}/reaction")
    public ReviewReactionResponse toggleReaction(
            @PathVariable Long reviewId,
            @RequestBody ReviewReactionRequest request,
            @AuthenticationPrincipal CurrentUserDetails userDetails
    ) {
        // 인증객체에서 userId 추출 후, UserService로 User 엔티티 획득
        if (userDetails == null) throw new RuntimeException("로그인 후 이용하세요");
        Long userId = userDetails.getUserId();
        User user = userService.getUserEntityById(userId);

        return reviewService.toggleReaction(reviewId, user, request.getReaction());
    }

    // 리뷰 반응 상태 조회 (Ajax)
    @GetMapping("/{reviewId}/reaction")
    public ReviewReactionResponse getReactionStatus(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal CurrentUserDetails userDetails
    ) {
        User user = null;
        if (userDetails != null) {
            user = userService.getUserEntityById(userDetails.getUserId());
        }
        return reviewService.getReactionStatus(reviewId, user);
    }
}







