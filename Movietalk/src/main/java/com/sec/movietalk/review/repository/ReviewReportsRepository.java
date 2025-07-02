package com.sec.movietalk.review.repository;

import com.sec.movietalk.common.domain.review.ReviewReports;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewReportsRepository extends JpaRepository<ReviewReports, Long> {
    // 특정 유저가 특정 리뷰를 이미 신고했는지 확인
    boolean existsByReview_ReviewIdAndUser_UserId(Long reviewId, Long userId);
}
