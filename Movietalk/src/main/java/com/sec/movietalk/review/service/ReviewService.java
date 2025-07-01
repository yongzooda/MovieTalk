package com.sec.movietalk.review.service;

import com.sec.movietalk.common.domain.review.Review;
import com.sec.movietalk.common.domain.review.ReviewReactions;
import com.sec.movietalk.common.domain.review.ReviewReactions.ReactionType;
import com.sec.movietalk.common.domain.user.User;
import com.sec.movietalk.review.dto.ReviewReactionRequest;
import com.sec.movietalk.review.dto.ReviewReactionResponse;
import com.sec.movietalk.review.repository.ReviewReactionsRepository;
import com.sec.movietalk.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewReactionsRepository reviewReactionsRepository;

    // 1. 리뷰 저장
    public void saveReview(Review review) {
        reviewRepository.save(review);
    }

    // 2. 리뷰 전체 목록 조회
    public List<Review> getReviewList() {
        return reviewRepository.findAll();
    }

    // 3. 리뷰 상세 조회 (예시)
    public Review getReviewDetail(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("리뷰가 존재하지 않습니다."));
    }

    // 4. 댓글 목록 조회 (예시)
    public List<Object> getComments(Long reviewId) {
        // 댓글 기능 구현 안 했으면 빈 리스트 반환하거나 생략해도 됨
        return List.of();
    }

    // 좋아요/싫어요 토글
    @Transactional
    public ReviewReactionResponse toggleReaction(Long reviewId, User user, String reactionStr) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰가 존재하지 않습니다."));
        ReactionType reactionType = ReactionType.valueOf(reactionStr);

        ReviewReactions reaction = reviewReactionsRepository.findByReviewAndUser(review, user).orElse(null);

        if (reaction != null) {
            // 같은 반응이면 삭제(토글 OFF)
            if (reaction.getReaction() == reactionType) {
                reviewReactionsRepository.delete(reaction);
            } else {
                reaction.setReaction(reactionType);
                reviewReactionsRepository.save(reaction);
            }
        } else {
            // 처음이면 생성
            ReviewReactions newReaction = ReviewReactions.builder()
                    .user(user)
                    .review(review)
                    .reaction(reactionType)
                    .build();
            reviewReactionsRepository.save(newReaction);
        }

        long likeCount = reviewReactionsRepository.countByReviewAndReaction(review, ReactionType.like);
        long dislikeCount = reviewReactionsRepository.countByReviewAndReaction(review, ReactionType.dislike);

        // 내 반응
        String myReaction = reviewReactionsRepository.findByReviewAndUser(review, user)
                .map(r -> r.getReaction().name())
                .orElse(null);

        return new ReviewReactionResponse(likeCount, dislikeCount, myReaction);
    }

    @Transactional(readOnly = true)
    public ReviewReactionResponse getReactionStatus(Long reviewId, User user) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰가 존재하지 않습니다."));

        long likeCount = reviewReactionsRepository.countByReviewAndReaction(review, ReactionType.like);
        long dislikeCount = reviewReactionsRepository.countByReviewAndReaction(review, ReactionType.dislike);

        String myReaction = null;
        if (user != null) {
            myReaction = reviewReactionsRepository.findByReviewAndUser(review, user)
                    .map(r -> r.getReaction().name())
                    .orElse(null);
        }
        return new ReviewReactionResponse(likeCount, dislikeCount, myReaction);
    }
}





















