package com.sec.movietalk.review.service;

import com.sec.movietalk.common.domain.movie.MovieCache;
import com.sec.movietalk.common.domain.review.Review;
import com.sec.movietalk.common.domain.review.ReviewReactions;
import com.sec.movietalk.common.domain.review.ReviewReactions.ReactionType;
import com.sec.movietalk.common.domain.review.ReviewReports;
import com.sec.movietalk.common.domain.user.User;
import com.sec.movietalk.recommendation.repository.MovieCacheRepository;
import com.sec.movietalk.review.dto.ReviewCreateRequest;
import com.sec.movietalk.review.dto.ReviewListResponse;
import com.sec.movietalk.review.dto.ReviewResponse;
import com.sec.movietalk.review.dto.ReviewUpdateRequest;
import com.sec.movietalk.review.repository.ReviewRepository;
import com.sec.movietalk.review.repository.ReviewReactionsRepository;
import com.sec.movietalk.review.repository.ReviewReportsRepository2;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    /** 신고 숨김 기준: 5회 이상 **/
    private static final int MAX_REPORTS = 5;

    private final ReviewRepository reviewRepository;
    private final MovieCacheRepository movieCacheRepository;
    private final ReviewReactionsRepository reactRepo;
    private final ReviewReportsRepository2 reportRepo;

    @Transactional(readOnly = true)
    public List<ReviewListResponse> getAllReviews() {
        // 신고 5회 미만 리뷰만 가져옴
        List<Review> reviews = reviewRepository.findAll().stream()
                .filter(r -> r.getReports().size() < MAX_REPORTS)
                .collect(Collectors.toList());

        Map<Integer, String> titleMap = movieCacheRepository
                .findAllById(reviews.stream()
                        .map(Review::getMovieId)
                        .collect(Collectors.toList()))
                .stream()
                .collect(Collectors.toMap(MovieCache::getMovieId, MovieCache::getTitle));

        return reviews.stream()
                .map(r -> {
                    String title  = titleMap.getOrDefault(r.getMovieId(), "제목 없음");
                    String author = r.getUser().getNickname();
                    return ReviewListResponse.fromEntity(r, title, author);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReviewListResponse> searchByMovieTitle(String keyword) {
        // 검색 결과 중 신고 5회 미만 필터
        List<Review> reviews = reviewRepository.searchByMovieTitleInCache(keyword).stream()
                .filter(r -> r.getReports().size() < MAX_REPORTS)
                .collect(Collectors.toList());

        Map<Integer, String> titleMap = movieCacheRepository
                .findAllByTitleContainingIgnoreCase(keyword)
                .stream()
                .collect(Collectors.toMap(MovieCache::getMovieId, MovieCache::getTitle));

        return reviews.stream()
                .map(r -> {
                    String title  = titleMap.getOrDefault(r.getMovieId(), "제목 없음");
                    String author = r.getUser().getNickname();
                    return ReviewListResponse.fromEntity(r, title, author);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReviewResponse getReviewById(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("리뷰를 찾을 수 없습니다. id=" + reviewId));

        // 신고 기준 초과 시 숨김 처리
        if (review.getReports().size() >= MAX_REPORTS) {
            throw new RuntimeException("삭제된 리뷰입니다.");
        }

        MovieCache cache = movieCacheRepository.findById(review.getMovieId())
                .orElseThrow(() -> new RuntimeException("영화 정보를 찾을 수 없습니다. id=" + review.getMovieId()));

        return ReviewResponse.fromEntity(
                review,
                review.getUser().getNickname(),
                cache.getTitle(),
                cache.getPosterUrl()
        );
    }

    @Transactional
    public void createReview(ReviewCreateRequest req) {
        List<MovieCache> movies = movieCacheRepository
                .findAllByTitleContainingIgnoreCase(req.getMovieTitle());

        if (movies.isEmpty()) {
            throw new IllegalArgumentException("등록된 영화가 아닙니다: " + req.getMovieTitle());
        }

        MovieCache movie = movies.stream()
                .filter(m -> m.getTitle().equalsIgnoreCase(req.getMovieTitle()))
                .findFirst()
                .orElse(movies.get(0));

        Review review = Review.builder()
                .movieId(movie.getMovieId())
                .user(new User(req.getUserId()))
                .content(req.getContent())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

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
        reviewRepository.deleteById(reviewId);
    }

    /**
     * 해당 유저가 이 리뷰에 대해 어떤 반응을 했는지 조회 (없으면 Optional.empty())
     */
    @Transactional(readOnly = true)
    public Optional<ReactionType> getUserReaction(Long reviewId, Long userId) {
        return reactRepo.findByReview_ReviewIdAndUser_UserId(reviewId, userId)
                .map(ReviewReactions::getReaction);
    }

    /**
     * 좋아요/싫어요 처리.
     * - 같은 버튼을 다시 누르면 취소
     * - 반대 버튼은 서버에서 무시 (뷰에서 disabled 처리)
     */
    @Transactional
    public void react(Long reviewId, ReactionType requestedType, User currentUser) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("리뷰를 찾을 수 없습니다. id=" + reviewId));

        Optional<ReviewReactions> existing =
                reactRepo.findByReview_ReviewIdAndUser_UserId(reviewId, currentUser.getUserId());

        if (existing.isPresent()) {
            ReviewReactions r = existing.get();
            if (r.getReaction() == requestedType) {
                // 같은 버튼 또 누르면 취소
                reactRepo.delete(r);
            }
            // 반대 버튼 클릭은 무시
        } else {
            // 최초 반응 저장
            ReviewReactions r = new ReviewReactions();
            r.setReview(review);
            r.setUser(currentUser);
            r.setReaction(requestedType);
            r.setCreatedAt(LocalDateTime.now());
            reactRepo.save(r);
        }

        // 카운트 동기화
        int likes    = (int) reactRepo.countByReview_ReviewIdAndReaction(reviewId, ReactionType.like);
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
