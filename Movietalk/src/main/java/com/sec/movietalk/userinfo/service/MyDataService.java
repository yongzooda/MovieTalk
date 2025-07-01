package com.sec.movietalk.userinfo.service;

import com.sec.movietalk.common.domain.movie.MovieCache;
import com.sec.movietalk.common.domain.review.Review;
import com.sec.movietalk.common.domain.user.User;
import com.sec.movietalk.recommendation.repository.MovieCacheRepository;
import com.sec.movietalk.review.dto.ReviewResponse;
import com.sec.movietalk.review.repository.ReviewRepository;
import com.sec.movietalk.userinfo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MyDataService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final MovieCacheRepository movieCacheRepository; // 기존 r

    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsByUserId(Long userId) {
        List<Review> reviews = reviewRepository.findByUser_UserId(userId);
        return reviews.stream()
                .map(review -> {
                    User user = userRepository.findById(review.getUserId()).orElseThrow();
                    MovieCache movie = movieCacheRepository.findById(review.getMovieId()).orElseThrow();
                    return ReviewResponse.fromEntity(
                            review,
                            user.getNickname(),
                            movie.getTitle(),
                            movie.getPosterUrl()
                    );
                })
                .toList();
    }

}
