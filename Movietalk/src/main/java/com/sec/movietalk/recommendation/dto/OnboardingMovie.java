package com.sec.movietalk.recommendation.dto;

import lombok.Builder;
import lombok.Getter;

// 온보딩 될 페이지
@Getter
@Builder
public class OnboardingMovie {
    private Integer movieId;   // MovieCache.movieId
    private String  title;
    private String  posterUrl; // MovieCache.posterUrl
}
