package com.sec.movietalk.review.service;

import com.sec.movietalk.common.domain.movie.MovieCache;
import com.sec.movietalk.common.domain.review.Review;
import com.sec.movietalk.common.domain.review.ReviewReactions;
import com.sec.movietalk.common.domain.review.ReviewReactions.ReactionType;
import com.sec.movietalk.common.domain.review.ReviewReports;
import com.sec.movietalk.common.domain.user.User;
import com.sec.movietalk.common.domain.comment.Comment;
import com.sec.movietalk.movie.dto.MovieDetailDto;
import com.sec.movietalk.movie.service.MovieService;
import com.sec.movietalk.recommendation.repository.MovieCacheRepository;
import com.sec.movietalk.review.dto.ReviewCreateRequest;
import com.sec.movietalk.review.dto.ReviewListResponse;
import com.sec.movietalk.review.dto.ReviewResponse;
import com.sec.movietalk.review.dto.ReviewUpdateRequest;
import com.sec.movietalk.review.repository.CommentRepository;
import com.sec.movietalk.review.repository.ReviewRepository;
import com.sec.movietalk.review.repository.ReviewReactionsRepository;
import com.sec.movietalk.review.repository.ReviewReportsRepository2;
import com.sec.movietalk.userinfo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private static final int MAX_REPORTS = 5;
    private final ReviewRepository reviewRepository;
    private final MovieCacheRepository movieCacheRepository;
    private final ReviewReactionsRepository reactRepo;
    private final ReviewReportsRepository2 reportRepo;
    private final MovieService movieService;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    @Transactional(readOnly = true)
    public List<ReviewListResponse> getAllReviews() {
        List<Review> reviews = reviewRepository.findAll().stream()
                .filter(r -> r.getReports().size() < MAX_REPORTS)
                .collect(Collectors.toList());
        Map<Integer, String> titleMap = movieCacheRepository
                .findAllById(reviews.stream().map(Review::getMovieId).collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(MovieCache::getMovieId, MovieCache::getTitle));
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
        List<Review> reviews = reviewRepository.searchByMovieTitleInCache(keyword).stream()
                .filter(r -> r.getReports().size() < MAX_REPORTS)
                .collect(Collectors.toList());
        Map<Integer, String> titleMap = movieCacheRepository
                .findAllByTitleContainingIgnoreCase(keyword)
                .stream().collect(Collectors.toMap(MovieCache::getMovieId, MovieCache::getTitle));
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
        if (review.getReports().size() >= MAX_REPORTS) {
            throw new RuntimeException("삭제된 리뷰입니다.");
        }
        MovieCache cache = movieCacheRepository.findById(review.getMovieId())
                .orElseThrow(() -> new RuntimeException("영화 정보를 찾을 수 없습니다. id=" + review.getMovieId()));
        String author = review.getUser().getNickname();
        String title = cache.getTitle();
        String posterUrl = cache.getPosterUrl();
        return ReviewResponse.fromEntity(review, author, title, posterUrl);
    }

    @Transactional
    public void createReview(ReviewCreateRequest req) {
        Integer movieId = req.getMovieId();
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
            movieCacheRepository.flush();
        }
        MovieCache movie = movieCacheRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("영화 정보를 찾을 수 없습니다. id=" + movieId));

        // ⭐️ 별점 미입력(null) 시 0점으로 저장
        Double rating = req.getRating();
        if (rating == null) rating = 0.0;

        Review review = Review.builder()
                .movieId(movie.getMovieId())
                .user(new User(req.getUserId()))
                .content(req.getContent())
                .rating(rating)
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

        Double rating = request.getRating();
        if (rating == null) rating = 0.0;

        review.setContent(request.getContent());
        review.setRating(rating);
        review.setUpdatedAt(LocalDateTime.now());
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        List<Comment> comments = commentRepository.findByReview_ReviewId(reviewId);
        Map<Long, Long> commentCountMap = comments.stream()
                .collect(Collectors.groupingBy(
                        c -> c.getUser().getUserId(),
                        Collectors.counting()
                ));
        commentCountMap.forEach((userId, count) -> {
            userRepository.incrementCommentCount(userId, -count.intValue());
        });
        Long userId = getReviewById(reviewId).getUserId();
        userRepository.incrementReviewCount(userId, -1);
        reviewRepository.deleteById(reviewId);
    }

    @Transactional(readOnly = true)
    public Optional<ReactionType> getUserReaction(Long reviewId, Long userId) {
        return reactRepo.findByReview_ReviewIdAndUser_UserId(reviewId, userId)
                .map(ReviewReactions::getReaction);
    }

    @Transactional
    public void react(Long reviewId, ReactionType requestedType, User currentUser) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("리뷰를 찾을 수 없습니다. id=" + reviewId));
        Optional<ReviewReactions> existing =
                reactRepo.findByReview_ReviewIdAndUser_UserId(reviewId, currentUser.getUserId());
        if (existing.isPresent()) {
            ReviewReactions r = existing.get();
            if (r.getReaction() == requestedType) {
                reactRepo.delete(r);
            }
        } else {
            ReviewReactions r = new ReviewReactions();
            r.setReview(review);
            r.setUser(currentUser);
            r.setReaction(requestedType);
            r.setCreatedAt(LocalDateTime.now());
            reactRepo.save(r);
        }
        int likes = (int) reactRepo.countByReview_ReviewIdAndReaction(reviewId, ReactionType.like);
        int dislikes = (int) reactRepo.countByReview_ReviewIdAndReaction(reviewId, ReactionType.dislike);
        review.setLikeCount(likes);
        review.setDislikeCount(dislikes);
    }

    @Transactional
    public void report(Long reviewId, String reason, User currentUser) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("리뷰를 찾을 수 없습니다. id=" + reviewId));
        if (reportRepo.findByReview_ReviewIdAndUser_UserId(reviewId, currentUser.getUserId()).isPresent()) {
            throw new IllegalArgumentException("이미 신고한 리뷰입니다.");
        }
        ReviewReports rpt = ReviewReports.builder()
                .review(review)
                .user(currentUser)
                .reason(reason)
                .build();
        reportRepo.save(rpt);
    }
}
