package com.sec.movietalk.review.repository;

import com.sec.movietalk.common.domain.review.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}


