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

    private String movieTitle;

    private Long userId;
    private String content;
    private LocalDateTime createdAt;
    private int likeCount; //



    public static ReviewListResponse fromEntity(Review review, String movieTitle) {
        return new ReviewListResponse(
                review.getId(),
                review.getMovieId(),
                movieTitle,
                review.getUserId(),
                review.getContent(),
                review.getCreatedAt(),
                review.getLikeCount() //
        );
    }
}





