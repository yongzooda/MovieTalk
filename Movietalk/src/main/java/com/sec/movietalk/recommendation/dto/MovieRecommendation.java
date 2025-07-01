package com.sec.movietalk.recommendation.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MovieRecommendation {
    private Long movie_id;
    private String title;
    private String poster_path;
    private Double vote_average;
    private Double popularity;
}