package com.sec.movietalk.review.controller;

import com.sec.movietalk.review.dto.ReviewCreateRequest;
import com.sec.movietalk.review.dto.ReviewUpdateRequest;
import com.sec.movietalk.review.dto.ReviewListResponse;
import com.sec.movietalk.review.dto.ReviewResponse;
import com.sec.movietalk.review.service.ReviewService;
import com.sec.movietalk.common.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.sec.movietalk.userinfo.security.CurrentUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public String listReviews(Model model) {
        List<ReviewListResponse> reviews = reviewService.getAllReviews();
        model.addAttribute("reviews", reviews);
        return "review/review_list";
    }

    @GetMapping("/search")
    public String searchReviews(@RequestParam("keyword") String keyword, Model model) {
        List<ReviewListResponse> results = reviewService.searchByMovieTitle(keyword);
        model.addAttribute("reviews", results);
        return "review/review_list";
    }

    @GetMapping("/{reviewId}")
    public String getReview(@PathVariable Long reviewId, Model model) {
        ReviewResponse review = reviewService.getReviewById(reviewId);
        model.addAttribute("review", review);
        return "review/review_detail";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("review", new ReviewCreateRequest());
        return "review/review_form";
    }

    @PostMapping
    public String createReview(@ModelAttribute ReviewCreateRequest request,
                               @AuthenticationPrincipal CurrentUserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        request.setUserId(userDetails.getUserId());
        reviewService.createReview(request);
        return "redirect:/reviews";
    }

    @PutMapping
    @ResponseBody
    public void updateReview(@RequestBody ReviewUpdateRequest request) {
        reviewService.updateReview(request);
    }

    @DeleteMapping("/{reviewId}")
    @ResponseBody
    public void deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
    }

    // 좋아요/싫어요 추가
    @PostMapping("/{reviewId}/like")
    public String likeReview(@PathVariable Long reviewId) {
        reviewService.increaseLike(reviewId);
        return "redirect:/reviews/" + reviewId;
    }

    @PostMapping("/{reviewId}/dislike")
    public String dislikeReview(@PathVariable Long reviewId) {
        reviewService.increaseDislike(reviewId);
        return "redirect:/reviews/" + reviewId;
    }
}







