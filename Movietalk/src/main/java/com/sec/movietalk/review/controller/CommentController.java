package com.sec.movietalk.review.controller;

import com.sec.movietalk.common.domain.user.User;
import com.sec.movietalk.common.util.UserUtil;
import com.sec.movietalk.review.dto.CommentRequest;
import com.sec.movietalk.review.dto.CommentResponse;
import com.sec.movietalk.review.service.CommentService;
import com.sec.movietalk.userinfo.repository.UserRepository;
import com.sec.movietalk.userinfo.security.CurrentUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews/{reviewId}/comments")
public class CommentController {

    private final CommentService commentService;

    private final UserRepository userRepository;  // ← User 엔티티 조회용


    /** 1) 해당 리뷰의 댓글 리스트 조회 */
    @GetMapping
    public List<CommentResponse> list(@PathVariable Long reviewId) {
        return commentService.getComments(reviewId);
    }

    /** 2) 새 댓글 작성 */
    @PostMapping
    public CommentResponse add(
            @PathVariable Long reviewId,
            @RequestBody    CommentRequest     req,
            @AuthenticationPrincipal Object principal
    ) {

        Long userId = UserUtil.extractUserId(principal);
        // 1) PrincipalDetails → 실제 User 엔티티 로딩
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자 못 찾음"));

        // 2) payload 준비
        CommentRequest payload = new CommentRequest(
                reviewId,
                null,
                req.content()
        );

        // 3) 서비스 호출
        return commentService.addComment(payload, user);
    }

    /** 3) 대댓글 작성 */
    @PostMapping("/{parentId}/reply")
    public CommentResponse reply(@PathVariable Long reviewId,
                                 @PathVariable Long parentId,
                                 @RequestBody CommentRequest req,
                                 @AuthenticationPrincipal Object principal) {

        Long userId = UserUtil.extractUserId(principal);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자 못 찾음"));

        CommentRequest payload = new CommentRequest(
                reviewId,
                parentId,
                req.content()
        );
        return commentService.reply(payload, user);
    }
}
