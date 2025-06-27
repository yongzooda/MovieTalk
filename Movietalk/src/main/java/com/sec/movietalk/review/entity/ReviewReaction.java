package com.sec.movietalk.review.entity;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewReaction {
    public enum ReactionType {
        LIKE, DISLIKE
    }

    private Long id;
    private Long reviewId;
    private Long userId;
    private ReactionType reaction;
    private LocalDateTime createdAt;
}
