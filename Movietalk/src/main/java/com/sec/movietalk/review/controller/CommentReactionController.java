package com.sec.movietalk.review.controller;

import com.sec.movietalk.common.domain.user.User;
import com.sec.movietalk.review.dto.ReactionRequest;
import com.sec.movietalk.review.service.CommentService;
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
            @AuthenticationPrincipal User currentUser
    ) {
        commentService.react(commentId, req.reactionType(), currentUser);
    }
}
