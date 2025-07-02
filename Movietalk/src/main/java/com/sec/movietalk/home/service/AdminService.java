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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;


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

    public void deleteReview(Long ReviewId) {

        reviewRepository.deleteById(ReviewId);
    }

    public void deleteUser(Long UserId) {

        userRepository.deleteById(UserId);
    }

    public void deleteComment(Long CommentId) {

        commentRepository.deleteById(CommentId);
    }

    public void deleteCommentReport(Long commentReportId) {

        commentReportsRepository.deleteById(commentReportId);
    }

    public void deleteReviewReport(Long reviewReportId) {

        reviewReportsRepository.deleteById(reviewReportId);
    }
}

