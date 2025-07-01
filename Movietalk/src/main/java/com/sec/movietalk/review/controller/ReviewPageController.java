package com.sec.movietalk.review.controller;

import com.sec.movietalk.common.domain.review.Review;
import com.sec.movietalk.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewPageController {

    private final ReviewService reviewService;

    // 리뷰 리스트 페이지
    @GetMapping
    public String reviewList(Model model) {
        model.addAttribute("reviews", reviewService.getReviewList());
        return "review/review_list";
    }

    // 리뷰 작성 폼
    @GetMapping("/new")
    public String reviewForm(Model model) {
        model.addAttribute("review", new Review());
        return "review/review_form";
    }

    // 리뷰 작성 완료(POST) 처리
    @PostMapping
    public String submitReview(@ModelAttribute("review") Review review) {
        review.setCreatedAt(LocalDateTime.now());    // ★ createdAt 세팅 반드시 추가!
        reviewService.saveReview(review);
        return "redirect:/reviews";
    }

    // 리뷰 상세 페이지
    @GetMapping("/{id}")
    public String reviewDetail(@PathVariable Long id, Model model) {
        model.addAttribute("review", reviewService.getReviewDetail(id));
        model.addAttribute("commentList", reviewService.getComments(id));
        return "review/review_detail";
    }
}



