package com.sec.movietalk.review.dto;

import java.time.LocalDateTime;
import java.util.List;

// 댓글 반환용
public record CommentResponse(
        Long id,
        String authorName,
        String content,
        LocalDateTime createdAt,
        Integer likeCnt,
        Integer dislikeCnt,
        Boolean accepted,
        List<CommentResponse> replies
) {}