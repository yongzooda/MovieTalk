package com.sec.movietalk.review.service;

import com.sec.movietalk.common.domain.review.Review;
import com.sec.movietalk.common.domain.user.User;
import com.sec.movietalk.common.domain.movie.MovieCache;
import com.sec.movietalk.recommendation.repository.MovieCacheRepository;
import com.sec.movietalk.review.dto.ReviewCreateRequest;
import com.sec.movietalk.review.dto.ReviewUpdateRequest;
import com.sec.movietalk.review.dto.ReviewListResponse;
import com.sec.movietalk.review.dto.ReviewResponse;
import com.sec.movietalk.review.repository.ReviewRepository;
import com.sec.movietalk.userinfo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final MovieCacheRepository movieCacheRepository;

    @Transactional(readOnly = true)
    public List<ReviewListResponse> getAllReviews() {
        List<Review> reviews = reviewRepository.findAll();

        Map<Integer, String> titleMap = movieCacheRepository.findAllById(
                        reviews.stream().map(Review::getMovieId).toList()
                ).stream()
                .collect(Collectors.toMap(MovieCache::getMovieId, MovieCache::getTitle));

        Map<Long, String> userMap = userRepository.findAllById(
                        reviews.stream().map(Review::getUserId).toList()
                ).stream()
                .collect(Collectors.toMap(User::getId, User::getNickname));

        return reviews.stream()
                .map(r -> {
                    String movieTitle = titleMap.getOrDefault(r.getMovieId(), "제목 없음");
                    String author = userMap.getOrDefault(r.getUserId(), "알 수 없음");
                    return ReviewListResponse.fromEntity(r, movieTitle, author);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReviewListResponse> searchByMovieTitle(String keyword) {
        List<MovieCache> matchedMovies = movieCacheRepository.findAllByTitleContainingIgnoreCase(keyword);
        List<Integer> matchedMovieIds = matchedMovies.stream()
                .map(MovieCache::getMovieId)
                .toList();

        List<Review> reviews = reviewRepository.findAllByMovieIdIn(matchedMovieIds);

        Map<Integer, String> titleMap = matchedMovies.stream()
                .collect(Collectors.toMap(MovieCache::getMovieId, MovieCache::getTitle));

        Map<Long, String> userMap = userRepository.findAllById(
                        reviews.stream().map(Review::getUserId).toList()
                ).stream()
                .collect(Collectors.toMap(User::getId, User::getNickname));

        return reviews.stream()
                .map(r -> {
                    String movieTitle = titleMap.getOrDefault(r.getMovieId(), "제목 없음");
                    String author = userMap.getOrDefault(r.getUserId(), "알 수 없음");
                    return ReviewListResponse.fromEntity(r, movieTitle, author);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public ReviewResponse getReviewById(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("리뷰를 찾을 수 없습니다."));

        String author = userRepository.findById(review.getUserId())
                .map(User::getNickname)
                .orElse("알 수 없음");

        String movieTitle = movieCacheRepository.findById(review.getMovieId())
                .map(MovieCache::getTitle)
                .orElse("제목 없음");
        String moviePosterUrl = movieCacheRepository.findById(review.getMovieId())
                .map(MovieCache::getPosterUrl)
                .orElse("");

        return ReviewResponse.fromEntity(review, author, movieTitle, moviePosterUrl);
    }

    @Transactional
    public void createReview(ReviewCreateRequest request) {
        LocalDateTime now = LocalDateTime.now();

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("해당 유저를 찾을 수 없습니다."));

        Review review = Review.builder()
                .movieId(request.getMovieId())
                .user(user)
                .content(request.getContent())
                .createdAt(now)
                .updatedAt(now)
                .likeCount(0)
                .dislikeCount(0)
                .build();

        reviewRepository.save(review);
    }

    @Transactional
    public void updateReview(ReviewUpdateRequest request) {
        Review review = reviewRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("리뷰를 찾을 수 없습니다."));

        review.setContent(request.getContent());
        review.setUpdatedAt(LocalDateTime.now());
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        reviewRepository.deleteById(reviewId);
    }

    // 좋아요/싫어요 기능
    @Transactional
    public void increaseLike(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("리뷰를 찾을 수 없습니다."));
        review.setLikeCount(review.getLikeCount() + 1);
    }

    @Transactional
    public void increaseDislike(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("리뷰를 찾을 수 없습니다."));
        review.setDislikeCount(review.getDislikeCount() + 1);
    }
}



















