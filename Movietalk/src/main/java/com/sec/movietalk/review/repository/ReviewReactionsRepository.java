package com.sec.movietalk.review.repository;

import com.sec.movietalk.common.domain.review.ReviewReactions;
import com.sec.movietalk.common.domain.review.ReviewReactions.ReactionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewReactionsRepository extends JpaRepository<ReviewReactions, Long> {
    // 특정 유저가 특정 리뷰에 남긴 기존 반응 조회
    Optional<ReviewReactions> findByReview_ReviewIdAndUser_UserId(Long reviewId, Long userId);

    // 리뷰별 좋아요 카운트 집계
    int countByReview_ReviewIdAndReaction(Long reviewId, ReactionType reaction);
}
