package com.sec.movietalk.review.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewCreateRequest {
    private Integer movieId;
    private String content;
}
