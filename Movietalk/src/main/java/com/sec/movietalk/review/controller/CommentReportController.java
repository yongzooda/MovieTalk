package com.sec.movietalk.review.controller;

import com.sec.movietalk.common.domain.user.User;
import com.sec.movietalk.review.dto.ReportRequest;
import com.sec.movietalk.review.service.CommentService;
import com.sec.movietalk.userinfo.security.CurrentUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments/{commentId}/reports")
@RequiredArgsConstructor
public class CommentReportController {

    private final CommentService commentService;

    /** 5) 댓글 신고 **/
    @PostMapping
    public void report(
            @PathVariable Long commentId,
            @RequestBody ReportRequest req,
            @AuthenticationPrincipal CurrentUserDetails principal  // ① UserDetails 로 받기
    ) {
        // ② UserDetails → 도메인 User 변환
        User currentUser = new User(principal.getUserId());
        commentService.report(commentId, req.reason(), currentUser);
    }
}
