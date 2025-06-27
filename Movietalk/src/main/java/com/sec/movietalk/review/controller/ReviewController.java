package com.sec.movietalk.review.controller;

import com.sec.movietalk.review.dto.*;
import com.sec.movietalk.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public String listReviews(Model model) {
        model.addAttribute("reviews", reviewService.getAllReviews());
        return "review/list";
    }

    @GetMapping("/{id}")
    public String getReviewDetail(@PathVariable Long id, Model model) {
        model.addAttribute("review", reviewService.getReview(id));
        return "review/detail";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("reviewForm", new ReviewCreateRequest());
        return "review/form";
    }

    @PostMapping
    public String createReview(@ModelAttribute("reviewForm") ReviewCreateRequest request) {
        Long fakeUserId = 1L; // 실제 로그인 사용자로 대체 필요
        reviewService.createReview(request, fakeUserId);
        return "redirect:/reviews";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        ReviewResponse review = reviewService.getReview(id);
        ReviewUpdateRequest updateRequest = new ReviewUpdateRequest();
        updateRequest.setContent(review.getContent());
        model.addAttribute("reviewId", id);
        model.addAttribute("reviewForm", updateRequest);
        return "review/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateReview(@PathVariable Long id, @ModelAttribute("reviewForm") ReviewUpdateRequest request) {
        reviewService.updateReview(id, request);
        return "redirect:/reviews/" + id;
    }

    @PostMapping("/delete/{id}")
    public String deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return "redirect:/reviews";
    }
}