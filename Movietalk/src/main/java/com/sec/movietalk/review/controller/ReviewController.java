package com.sec.movietalk.review.controller;

import com.sec.movietalk.common.util.UserUtil;
import com.sec.movietalk.review.dto.CommentRequest;
import com.sec.movietalk.review.dto.CommentResponse;
import com.sec.movietalk.review.dto.ReviewCreateRequest;
import com.sec.movietalk.review.dto.ReviewListResponse;
import com.sec.movietalk.review.dto.ReviewResponse;
import com.sec.movietalk.review.dto.ReviewUpdateRequest;
import com.sec.movietalk.review.service.CommentService;
import com.sec.movietalk.review.service.ReviewService;
import com.sec.movietalk.common.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final CommentService commentService;

    /** 1) 전체 리뷰 목록 조회 */
    @GetMapping
    public String listReviews(Model model) {
        List<ReviewListResponse> reviews = reviewService.getAllReviews();
        model.addAttribute("reviews", reviews);
        return "review/review_list";
    }

    /** 2) 영화 제목 키워드로 리뷰 검색 */
    @GetMapping("/search")
    public String searchReviews(@RequestParam("keyword") String keyword, Model model) {
        List<ReviewListResponse> results = reviewService.searchByMovieTitle(keyword);
        model.addAttribute("reviews", results);
        return "review/review_list";
    }

    /** 3) 리뷰 상세 페이지 */
    @GetMapping("/{reviewId}")
    public String getReview(
            @PathVariable Long reviewId,
            Model model,
            @AuthenticationPrincipal Object principal   // 현재 로그인 정보
    ) {
        // 리뷰 데이터
        ReviewResponse review = reviewService.getReviewById(reviewId);

        // 댓글 리스트
        List<CommentResponse> commentList = commentService.getComments(reviewId);

        // 현재 로그인 유저 ID
        Long currentUserId = UserUtil.extractUserId(principal);

        // 뷰에 필요한 모델 속성 설정
        model.addAttribute("review", review);
        model.addAttribute("commentList", commentList);
        model.addAttribute("currentUserId", currentUserId);         // 로그인한 사용자
        model.addAttribute("reviewAuthorId", review.getUserId());   // 리뷰 작성자

        return "review/review_detail";
    }

    /** 4) 댓글 등록 처리 (폼 POST) */
    @PostMapping("/{reviewId}/comments")
    public String addComment(
            @PathVariable Long reviewId,
            @RequestParam String content,
            @AuthenticationPrincipal Object principal
    ) {
        CommentRequest payload = new CommentRequest(reviewId, null, content);
        User user = new User(UserUtil.extractUserId(principal));
        commentService.addComment(payload, user);
        return "redirect:/reviews/" + reviewId;
    }

    /** 5) 리뷰 작성 폼 */
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("review", new ReviewCreateRequest());
        return "review/review_form";
    }

    /** 6) 리뷰 작성 처리 */
    @PostMapping
    public String createReview(
            @ModelAttribute ReviewCreateRequest request,
            @AuthenticationPrincipal Object principal
    ) {
        Long userId = UserUtil.extractUserId(principal);
        request.setUserId(userId);
        reviewService.createReview(request);
        return "redirect:/reviews";
    }

    /** 7) 리뷰 수정 (AJAX 용) */
    @PutMapping
    @ResponseBody
    public void updateReview(@RequestBody ReviewUpdateRequest request) {
        reviewService.updateReview(request);
    }

    /** 8) 리뷰 삭제 (AJAX 용) */
    @DeleteMapping("/{reviewId}")
    @ResponseBody
    public void deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
    }
}
