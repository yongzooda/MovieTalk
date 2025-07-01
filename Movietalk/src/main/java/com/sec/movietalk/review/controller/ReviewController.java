package com.sec.movietalk.review.controller;

import com.sec.movietalk.review.dto.ReviewCreateRequest;
import com.sec.movietalk.review.dto.ReviewListResponse;
import com.sec.movietalk.review.dto.ReviewResponse;
import com.sec.movietalk.review.dto.ReviewUpdateRequest;
import com.sec.movietalk.review.dto.CommentResponse;
import com.sec.movietalk.review.service.ReviewService;
import com.sec.movietalk.review.service.CommentService;
import com.sec.movietalk.common.domain.user.User;
import lombok.RequiredArgsConstructor;
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

    /** 전체 리뷰 목록 조회 */
    @GetMapping
    public String listReviews(Model model) {
        List<ReviewListResponse> reviews = reviewService.getAllReviews();
        model.addAttribute("reviews", reviews);
        return "review/review_list";
    }

    /** 영화 제목 키워드로 리뷰 검색 */
    @GetMapping("/search")
    public String searchReviews(@RequestParam("keyword") String keyword, Model model) {
        List<ReviewListResponse> results = reviewService.searchByMovieTitle(keyword);
        model.addAttribute("reviews", results);
        return "review/review_list";
    }

    /** 리뷰 상세 페이지 */
    @GetMapping("/{reviewId}")
    public String getReview(@PathVariable Long reviewId, Model model) {
        // 리뷰 데이터
        ReviewResponse review = reviewService.getReviewById(reviewId);
        model.addAttribute("review", review);

        // 댓글 리스트
        List<CommentResponse> commentList = commentService.getComments(reviewId);
        model.addAttribute("commentList", commentList);

        // 프래그먼트 호출용
        return "review/reviewDetail";
    }

    /** 리뷰 작성 폼 */
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("review", new ReviewCreateRequest());
        return "review/review_form";
    }

    /** 리뷰 작성 처리 */
    @PostMapping
    public String createReview(@ModelAttribute ReviewCreateRequest request,
                               @SessionAttribute("loginUser") User sessionUser) {
        request.setUserId(sessionUser.getId());
        reviewService.createReview(request);
        return "redirect:/reviews";
    }

    /** 리뷰 수정 */
    @PutMapping
    @ResponseBody
    public void updateReview(@RequestBody ReviewUpdateRequest request) {
        reviewService.updateReview(request);
    }

    /** 리뷰 삭제 */
    @DeleteMapping("/{reviewId}")
    @ResponseBody
    public void deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
    }
}
