package com.sec.movietalk.review.service;

import com.sec.movietalk.review.dto.*;
import com.sec.movietalk.common.domain.review.Review;
import com.sec.movietalk.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    @Transactional
    public Long createReview(ReviewCreateRequest request, Long userId) {
        Review review = new Review(request.getMovieId(), userId, request.getContent());
        return reviewRepository.save(review).getId();
    }

    @Transactional
    public void updateReview(Long id, ReviewUpdateRequest request) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 리뷰 없음"));
        review.setContent(request.getContent());
    }

    @Transactional
    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public ReviewResponse getReview(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 리뷰 없음"));
        return new ReviewResponse(review);
    }

    @Transactional(readOnly = true)
    public List<ReviewListResponse> getAllReviews() {
        return reviewRepository.findAll().stream()
                .map(ReviewListResponse::new)
                .collect(Collectors.toList());
    }
}