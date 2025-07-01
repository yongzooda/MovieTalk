package com.sec.movietalk.review.repository;

import com.sec.movietalk.review.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}



