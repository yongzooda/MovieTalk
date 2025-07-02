package com.sec.movietalk.review.controller;

import com.sec.movietalk.common.domain.user.User;
import com.sec.movietalk.review.dto.ReactionRequest;
import com.sec.movietalk.review.dto.ReportRequest;
import com.sec.movietalk.review.service.ReviewService;
import com.sec.movietalk.userinfo.security.CurrentUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews/{reviewId}/reactions")
@RequiredArgsConstructor
public class ReviewReactionController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<Void> react(
            @PathVariable Long reviewId,
            @RequestBody ReactionRequest req,
            @AuthenticationPrincipal CurrentUserDetails currentUserDetails
    ) {
        // CurrentUserDetails → 도메인 User 변환
        User user = new User(currentUserDetails.getUserId());
        reviewService.reactReview(reviewId, req.reactionType(), user);
        return ResponseEntity.ok().build();
    }
}
