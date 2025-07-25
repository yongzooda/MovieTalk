package com.sec.movietalk.review.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewCreateRequest {
    private Long userId;
    private Integer movieId;
    private String movieTitle;
    private String content;
    private Double rating;
}
