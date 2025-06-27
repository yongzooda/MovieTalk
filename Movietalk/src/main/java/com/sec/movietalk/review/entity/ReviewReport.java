package com.sec.movietalk.review.entity;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewReport {
    private Long id;
    private Long reviewId;
    private Long userId;
    private String reason;
    private LocalDateTime createdAt;
}
