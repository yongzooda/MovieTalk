package com.sec.movietalk.review.controller;

import com.sec.movietalk.review.entity.Comment;
import com.sec.movietalk.review.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class CommentController {

    private final CommentRepository commentRepository;

    @PostMapping("/reviews/{reviewId}/comments")
    public String addComment(@PathVariable Long reviewId,
                             @RequestParam String author,
                             @RequestParam String content) {

        Comment comment = Comment.builder()
                .reviewId(reviewId)
                .author(author)
                .content(content)
                .createdAt(LocalDateTime.now())
                .build();

        commentRepository.save(comment);

        return "redirect:/reviews/" + reviewId;
    }
}


