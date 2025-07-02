package com.sec.movietalk.review.controller;

import com.sec.movietalk.common.domain.user.User;
import com.sec.movietalk.review.service.CommentService;
import com.sec.movietalk.userinfo.security.CurrentUserDetails;
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
            @AuthenticationPrincipal CurrentUserDetails principal  // UserDetails 로 받기
    ) {
        // UserDetails → 도메인 User 변환
        User currentUser = new User(principal.getUserId());
        commentService.adopt(commentId, currentUser);
    }
}
