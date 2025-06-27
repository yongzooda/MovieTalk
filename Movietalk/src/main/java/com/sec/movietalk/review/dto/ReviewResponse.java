package com.sec.movietalk.review.dto;

import com.sec.movietalk.common.domain.review.Review;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReviewResponse {
    private Long id;
    private String content;
    private int likeCount;
    private int dislikeCount;
    private LocalDateTime createdAt;

    public ReviewResponse(Review review) {
        this.id = review.getId();
        this.content = review.getContent();
        this.likeCount = review.getLikeCount();
        this.dislikeCount = review.getDislikeCount();
        this.createdAt = review.getCreatedAt();
    }
}