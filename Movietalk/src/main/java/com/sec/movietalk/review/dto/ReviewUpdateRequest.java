package com.sec.movietalk.review.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewUpdateRequest {
    private Long reviewId;
    private String content;
}




