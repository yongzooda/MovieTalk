package com.sec.movietalk.review.dto;

import com.sec.movietalk.common.domain.review.Review;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ReviewListResponse {
    private Long id;
    private Integer movieId;
    private Long userId;
    private String content;
    private LocalDateTime createdAt;

    public static ReviewListResponse fromEntity(Review review) {
        return new ReviewListResponse(
                review.getId(),
                review.getMovieId(),
                review.getUserId(),
                review.getContent(),
                review.getCreatedAt()
        );
    }
}



