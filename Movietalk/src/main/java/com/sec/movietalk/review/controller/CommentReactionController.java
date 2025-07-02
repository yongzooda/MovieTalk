package com.sec.movietalk.review.controller;

import com.sec.movietalk.common.domain.user.User;
import com.sec.movietalk.review.dto.ReactionRequest;
import com.sec.movietalk.review.service.CommentService;
import com.sec.movietalk.userinfo.security.CurrentUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments/{commentId}/reactions")
@RequiredArgsConstructor
public class CommentReactionController {

    private final CommentService commentService;

    /** 4) 댓글 좋아요/싫어요 **/
    @PostMapping
    public void react(
            @PathVariable Long commentId,
            @RequestBody ReactionRequest req,
            @AuthenticationPrincipal CurrentUserDetails principal  // UserDetails 로 받기
    ) {
        // UserDetails → 도메인 User 로 변환
        User currentUser = new User(principal.getUserId());
        commentService.react(commentId, req.reactionType(), currentUser);
    }
}
