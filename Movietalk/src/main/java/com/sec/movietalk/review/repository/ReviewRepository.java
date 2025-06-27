package com.sec.movietalk.review.repository;

import com.sec.movietalk.common.domain.review.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByMovieId(Integer movieId);

    List<Review> findByUserId(Long userId);

    List<Review> findByContentContaining(String keyword);
}
