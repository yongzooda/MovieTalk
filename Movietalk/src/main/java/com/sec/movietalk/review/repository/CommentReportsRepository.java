package com.sec.movietalk.review.repository;

import com.sec.movietalk.common.domain.comment.CommentReports;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CommentReportsRepository extends JpaRepository<CommentReports, Long> {
    // 유저와 댓글에 따른 신고 중복 방지 조회용 예시
    Optional<CommentReports> findByComment_CommentIdAndUser_UserId(Long commentId, Long userId);
}
