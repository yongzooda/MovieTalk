package com.sec.movietalk.review.controller;

import com.sec.movietalk.common.domain.user.User;
import com.sec.movietalk.review.dto.CommentRequest;
import com.sec.movietalk.review.dto.CommentResponse;
import com.sec.movietalk.review.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews/{reviewId}/comments")
public class CommentController {

    private final CommentService commentService;

    /** 1) 해당 리뷰의 댓글 리스트 조회 */
    @GetMapping
    public List<CommentResponse> list(@PathVariable Long reviewId) {
        return commentService.getComments(reviewId);
    }

    /** 2) 새 댓글 작성 */
    @PostMapping
    public CommentResponse add(@PathVariable Long reviewId,
                               @RequestBody CommentRequest req,
                               @AuthenticationPrincipal User currentUser) {
        CommentRequest payload = new CommentRequest(
                reviewId,
                null,
                req.content()
        );
        return commentService.addComment(payload, currentUser);
    }

    /** 3) 대댓글 작성 */
    @PostMapping("/{parentId}/reply")
    public CommentResponse reply(@PathVariable Long reviewId,
                                 @PathVariable Long parentId,
                                 @RequestBody CommentRequest req,
                                 @AuthenticationPrincipal User currentUser) {
        CommentRequest payload = new CommentRequest(
                reviewId,
                parentId,
                req.content()
        );
        return commentService.reply(payload, currentUser);
    }
}
