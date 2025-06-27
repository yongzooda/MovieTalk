package com.sec.movietalk.review.dto;

import com.sec.movietalk.common.domain.review.Review;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReviewListResponse {
    private Long id;
    private Integer movieId;
    private LocalDateTime createdAt;

    public ReviewListResponse(Review review) {
        this.id = review.getId();
        this.movieId = review.getMovieId();
        this.createdAt = review.getCreatedAt();
    }
}