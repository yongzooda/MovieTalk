package com.sec.movietalk.review.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewReactionRequest {
    private String reaction; // "like" 또는 "dislike"
}
