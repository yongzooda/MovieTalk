package com.sec.movietalk.review.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ReviewUpdateRequest {

    private Long reviewId;
    private Long userId;
    private Integer movieId;
    private String content;
    private String movieTitle;
    private Double rating;
}
