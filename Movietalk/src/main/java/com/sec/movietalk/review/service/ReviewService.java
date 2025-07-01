package com.sec.movietalk.review.service;

import com.sec.movietalk.common.domain.review.Review;
import com.sec.movietalk.common.domain.user.User;
import com.sec.movietalk.common.domain.movie.MovieCache;
import com.sec.movietalk.recommendation.repository.MovieCacheRepository; //
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
    private final MovieCacheRepository movieCacheRepository; // 기존 recommendation.repository 위치 사용

    @Transactional(readOnly = true)
    public List<ReviewListResponse> getAllReviews() {
        List<Review> reviews = reviewRepository.findAll();

        Map<Integer, String> titleMap = movieCacheRepository.findAllById(
                        reviews.stream().map(Review::getMovieId).toList()
                ).stream()
                .collect(Collectors.toMap(MovieCache::getMovieId, MovieCache::getTitle));

        return reviews.stream()
                .map(r -> {
                    // userId로 유저를 조회해서 닉네임(author)을 가져옴
                    User user = userRepository.findById(r.getUserId())
                            .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));
                    String author = user.getNickname();
                    String movieTitle = titleMap.getOrDefault(r.getMovieId(), "제목 없음");
                    return ReviewListResponse.fromEntity(r, movieTitle, author);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReviewListResponse> searchByMovieTitle(String keyword) {
        List<Review> reviews = reviewRepository.searchByMovieTitleInCache(keyword);

        Map<Integer, String> titleMap = movieCacheRepository.findAllByTitleContainingIgnoreCase(keyword)
                .stream()
                .collect(Collectors.toMap(MovieCache::getMovieId, MovieCache::getTitle));

        return reviews.stream()
                .map(r -> {
                    String movieTitle = titleMap.getOrDefault(r.getMovieId(), "제목 없음");
                    User user = userRepository.findById(r.getUserId())
                            .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));
                    String author = user.getNickname();
                    return ReviewListResponse.fromEntity(r, movieTitle, author);
                })
                .toList();

    }

    @Transactional(readOnly = true)
    public ReviewResponse getReviewById(Long reviewId) {

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("리뷰를 찾을 수 없습니다."));
        User user = userRepository.findById(review.getUserId())
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));
        MovieCache movie = movieCacheRepository.findById(review.getMovieId())
                .orElseThrow(() -> new RuntimeException("영화를 찾을 수 없습니다."));

        return ReviewResponse.fromEntity(
                review,
                user.getNickname(),         // 작성자 닉네임
                movie.getTitle(),           // 영화 제목
                movie.getPosterUrl()        // 영화 포스터
        );
    }

    @Transactional
    public void createReview(ReviewCreateRequest req) {
        // 1) 제목으로 캐시에서 찾아오기 (Contains → Exact match 우선)
        List<MovieCache> movies = movieCacheRepository
                .findAllByTitleContainingIgnoreCase(req.getMovieTitle());

        if (movies.isEmpty()) {
            throw new IllegalArgumentException(
                    "등록된 영화가 아닙니다: " + req.getMovieTitle());
        }

        // 정확히 일치하는 영화가 있으면 그걸, 없으면 첫 번째
        MovieCache movie = movies.stream()
                .filter(m -> m.getTitle().equalsIgnoreCase(req.getMovieTitle()))
                .findFirst()
                .orElse(movies.get(0));

        // 2) Review 엔티티에 movieId, userId, content 세팅
        Review review = Review.builder()
                .movieId(movie.getMovieId())
                .user(new User(req.getUserId()))      // User 생성자 혹은 조회
                .content(req.getContent())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        reviewRepository.save(review);
    }


    @Transactional
    public void updateReview(ReviewUpdateRequest request) {
        Review review = reviewRepository.findById(request.getReviewId())
                .orElseThrow(() -> new RuntimeException("리뷰를 찾을 수 없습니다."));

        review.setContent(request.getContent());
        review.setUpdatedAt(LocalDateTime.now());
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        reviewRepository.deleteById(reviewId);
    }
}

