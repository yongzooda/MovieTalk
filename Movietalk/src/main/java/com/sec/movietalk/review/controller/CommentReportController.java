package com.sec.movietalk.review.controller;

import com.sec.movietalk.common.domain.user.User;
import com.sec.movietalk.common.util.UserUtil;
import com.sec.movietalk.review.dto.ReportRequest;
import com.sec.movietalk.review.service.CommentService;
import com.sec.movietalk.userinfo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments/{commentId}/reports")
@RequiredArgsConstructor
public class CommentReportController {

    private final CommentService commentService;
    private final UserRepository userRepository;

    /** 5) 댓글 신고 **/
    @PostMapping
    public void report(
            @PathVariable Long commentId,
            @RequestBody ReportRequest req,
            @AuthenticationPrincipal Object principal
    ) {
        // 1) Principal 에서 사용자 ID 추출
        Long userId = UserUtil.extractUserId(principal);

        // 2) User 엔티티 조회
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        // 3) 서비스 호출
        commentService.report(commentId, req.reason(), currentUser);
    }
}
