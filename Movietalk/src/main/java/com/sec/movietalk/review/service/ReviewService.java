package com.sec.movietalk.review.service;

import com.sec.movietalk.common.domain.movie.MovieCache;
import com.sec.movietalk.common.domain.review.Review;
import com.sec.movietalk.common.domain.user.User;
import com.sec.movietalk.movie.dto.MovieDetailDto;
import com.sec.movietalk.movie.service.MovieService;
import com.sec.movietalk.recommendation.repository.MovieCacheRepository;
import com.sec.movietalk.review.dto.ReviewCreateRequest;
import com.sec.movietalk.review.dto.ReviewListResponse;
import com.sec.movietalk.review.dto.ReviewResponse;
import com.sec.movietalk.review.dto.ReviewUpdateRequest;
import com.sec.movietalk.review.repository.ReviewRepository;
import com.sec.movietalk.userinfo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final MovieCacheRepository movieCacheRepository;
    private final MovieService movieService;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<ReviewListResponse> getAllReviews() {
        List<Review> reviews = reviewRepository.findAll();

        Map<Integer, String> titleMap = movieCacheRepository
                .findAllById(reviews.stream()
                        .map(Review::getMovieId)
                        .collect(Collectors.toList()))
                .stream()
                .collect(Collectors.toMap(MovieCache::getMovieId, MovieCache::getTitle));

        return reviews.stream()
                .map(r -> {
                    String title = titleMap.getOrDefault(r.getMovieId(), "제목 없음");
                    String author = r.getUser().getNickname();
                    return ReviewListResponse.fromEntity(r, title, author);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReviewListResponse> searchByMovieTitle(String keyword) {
        List<Review> reviews = reviewRepository.searchByMovieTitleInCache(keyword);

        Map<Integer, String> titleMap = movieCacheRepository
                .findAllByTitleContainingIgnoreCase(keyword)
                .stream()
                .collect(Collectors.toMap(MovieCache::getMovieId, MovieCache::getTitle));

        return reviews.stream()
                .map(r -> {
                    String title = titleMap.getOrDefault(r.getMovieId(), "제목 없음");
                    String author = r.getUser().getNickname();
                    return ReviewListResponse.fromEntity(r, title, author);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReviewResponse getReviewById(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("리뷰를 찾을 수 없습니다. id=" + reviewId));

        MovieCache cache = movieCacheRepository.findById(review.getMovieId())
                .orElseThrow(() -> new RuntimeException("영화 정보를 찾을 수 없습니다. id=" + review.getMovieId()));

        String title = cache.getTitle();
        String posterUrl = cache.getPosterUrl();
        String author = review.getUser().getNickname();

        return ReviewResponse.fromEntity(review, author, title, posterUrl);
    }

    @Transactional
    public void createReview(ReviewCreateRequest req) {
        Integer movieId = req.getMovieId();

        // 캐시에 영화 정보가 없으면 TMDB에서 가져와 직접 저장
        if (!movieCacheRepository.existsById(movieId)) {
            MovieDetailDto dto = movieService.getMovieDetailFromTmdbId(movieId);

            MovieCache movie = MovieCache.builder()
                    .movieId(movieId)
                    .title(dto.getTitle())
                    .posterUrl("https://image.tmdb.org/t/p/w500" + dto.getPosterPath())
                    .overview(dto.getOverview())
                    .releaseDate(LocalDate.parse(dto.getReleaseDate()))
                    .build();

            movieCacheRepository.save(movie);
        }

        MovieCache movie = movieCacheRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("영화 정보를 찾을 수 없습니다. id=" + movieId));

        Review review = Review.builder()
                .movieId(movie.getMovieId())
                .user(new User(req.getUserId()))
                .content(req.getContent())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        userRepository.incrementReviewCount(req.getUserId(), 1);

        reviewRepository.save(review);
    }

    @Transactional
    public void updateReview(ReviewUpdateRequest request) {
        Review review = reviewRepository.findById(request.getReviewId())
                .orElseThrow(() -> new RuntimeException("리뷰를 찾을 수 없습니다. id=" + request.getReviewId()));

        review.setContent(request.getContent());
        review.setUpdatedAt(LocalDateTime.now());
    }

    @Transactional
    public void deleteReview(Long reviewId) {

        Long userId = getReviewById(reviewId).getUserId();


        userRepository.incrementReviewCount(userId, -1);

        reviewRepository.deleteById(reviewId);
    }
}
