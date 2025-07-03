package com.sec.movietalk.home.service;

import com.sec.movietalk.common.domain.comment.Comment;
import com.sec.movietalk.common.domain.comment.CommentReports;
import com.sec.movietalk.common.domain.review.Review;
import com.sec.movietalk.common.domain.review.ReviewReports;
import com.sec.movietalk.common.domain.user.User;
import com.sec.movietalk.home.repository.ReviewReportsRepository;
import com.sec.movietalk.review.repository.CommentReportsRepository;
import com.sec.movietalk.review.repository.CommentRepository;
import com.sec.movietalk.review.repository.ReviewRepository;
import com.sec.movietalk.userinfo.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final CommentRepository commentRepository;
    private final CommentReportsRepository commentReportsRepository;
    private final ReviewReportsRepository reviewReportsRepository;

    public Page<User> getUserPage(int page, int size) {

        return userRepository.findAll(PageRequest.of(page, size));
    }

    public Page<Review> getReviewPage(int page, int size) {

        return reviewRepository.findAll(PageRequest.of(page, size));
    }

    public Page<Comment> getCommentPage(int page, int size) {

        return commentRepository.findAll(PageRequest.of(page, size));
    }

    public Page<ReviewReports> getReviewReportsPage(int page, int size) {

        return reviewReportsRepository.findAll(PageRequest.of(page, size));
    }

    public Page<CommentReports> getCommentReportsPage(int page, int size) {

        return commentReportsRepository.findAll(PageRequest.of(page, size));
    }

    @Transactional
    public void deleteReview(Long ReviewId) {

        Review review = reviewRepository.findById(ReviewId)
                .orElseThrow(() -> new EntityNotFoundException("삭제할 리뷰가 없습니다: " + ReviewId));
        Long authorId = review.getUser().getUserId();
        // 2) 리뷰 삭제

        reviewRepository.deleteById(ReviewId);

        // 3) 작성자 reviewCnt 감소
        userRepository.incrementReviewCount(authorId, -1);
    }

    public void deleteUser(Long UserId) {

        userRepository.deleteById(UserId);
    }

    @Transactional
    public void deleteComment(Long CommentId) {

        Comment comment = commentRepository.findById(CommentId)
                .orElseThrow(() -> new EntityNotFoundException("삭제할 리뷰가 없습니다: " + CommentId));
        Long authorId = comment.getUser().getUserId();


        commentRepository.deleteById(CommentId);

        userRepository.incrementCommentCount(authorId, -1);
    }

    public void deleteCommentReport(Long commentReportId) {

        commentReportsRepository.deleteById(commentReportId);
    }

    public void deleteReviewReport(Long reviewReportId) {

        reviewReportsRepository.deleteById(reviewReportId);
    }
}

