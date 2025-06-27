package com.sec.movietalk.review.entity;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewViews {
    private Long id;
    private Long reviewId;
    private Long userId;
    private LocalDateTime viewedAt;
}

