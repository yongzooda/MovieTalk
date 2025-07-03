package com.sec.movietalk.review.repository;

import com.sec.movietalk.review.entity.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findByNickname(String nickname);

    void incrementReviewCount(Long userId, int i);
}

