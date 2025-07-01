package com.sec.movietalk.review.controller;

import com.sec.movietalk.common.domain.user.User;
import com.sec.movietalk.review.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments/{commentId}/adopt")
@RequiredArgsConstructor
public class CommentAdoptionController {

    private final CommentService commentService;

    /** 6) 댓글 채택 **/
    @PostMapping
    public void adopt(
            @PathVariable Long commentId,
            @AuthenticationPrincipal User currentUser
    ) {
        commentService.adopt(commentId, currentUser);
    }
}
