package com.sec.movietalk.review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReviewReactionResponse {
    private long likeCount;
    private long dislikeCount;
    private String myReaction; // "like" / "dislike" / null
}
