package com.sec.movietalk.review.controller;

import com.sec.movietalk.common.domain.user.User;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews/{reviewId}/comments")
public class CommentController {

    private final CommentService commentService;
    private final UserRepository userRepository;  // User 엔티티 조회용

    /** 1) 해당 리뷰의 댓글 리스트 조회 */
    @GetMapping
    public List<CommentResponse> list(@PathVariable Long reviewId) {
        return commentService.getComments(reviewId);
    }

    /** 2) 새 댓글 작성 */
    @PostMapping
    public CommentResponse add(
            @PathVariable Long reviewId,
            @RequestBody CommentRequest req,
            @AuthenticationPrincipal CurrentUserDetails currentUser  // UserDetails로 받기
    ) {
        // UserDetails → 실제 User 엔티티 로딩
        User user = userRepository.findById(currentUser.getUserId())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다. id=" + currentUser.getUserId()));

        // 서비스 호출
        return commentService.addComment(
                new CommentRequest(reviewId, null, req.content()),
                user
        );
    }

    /** 3) 대댓글 작성 */
    @PostMapping("/{parentId}/reply")
    public CommentResponse reply(
            @PathVariable Long reviewId,
            @PathVariable Long parentId,
            @RequestBody CommentRequest req,
            @AuthenticationPrincipal CurrentUserDetails currentUser  // UserDetails로 받기
    ) {
        // UserDetails → 도메인 User로 변환
        User user = userRepository.findById(currentUser.getUserId())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다. id=" + currentUser.getUserId()));

        // 서비스 호출
        return commentService.reply(
                new CommentRequest(reviewId, parentId, req.content()),
                user
        );
    }
}
