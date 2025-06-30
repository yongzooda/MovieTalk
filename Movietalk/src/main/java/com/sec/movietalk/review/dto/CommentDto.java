package com.sec.movietalk.review.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CommentDto(
        Long id,
        String author,
        String content,
        LocalDateTime createdAt
) {}

