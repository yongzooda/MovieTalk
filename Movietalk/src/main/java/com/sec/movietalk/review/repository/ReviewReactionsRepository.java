package com.sec.movietalk.review.repository;

import com.sec.movietalk.common.domain.review.Review;
import com.sec.movietalk.common.domain.review.ReviewReactions;
import com.sec.movietalk.common.domain.user.User;
import com.sec.movietalk.common.domain.review.ReviewReactions.ReactionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewReactionsRepository extends JpaRepository<ReviewReactions, Long> {
    Optional<ReviewReactions> findByReviewAndUser(Review review, User user);
    long countByReviewAndReaction(Review review, ReactionType reaction);
}
