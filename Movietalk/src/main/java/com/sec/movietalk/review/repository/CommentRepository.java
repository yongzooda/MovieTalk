package com.sec.movietalk.review.repository;

import com.sec.movietalk.common.domain.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    // 최상위 댓글(=parent null) 리스트
    List<Comment> findByReview_ReviewIdAndParentIsNullOrderByCreatedAt(Long reviewId);

    // 해당 댓글의 대댓글 리스트
    List<Comment> findByParent_CommentIdOrderByCreatedAt(Long parentId);

    List<Comment> findByUser_UserId(Long userId);
}


