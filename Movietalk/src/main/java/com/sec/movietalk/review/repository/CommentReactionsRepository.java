package com.sec.movietalk.review.repository;

import com.sec.movietalk.common.domain.comment.CommentReactions;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CommentReactionsRepository extends JpaRepository<CommentReactions, Long> {
    // 유저와 댓글에 따른 기존 반응 조회용 예시
    Optional<CommentReactions> findByComment_CommentIdAndUser_UserId(Long commentId, Long userId);
}
