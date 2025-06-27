package com.sec.movietalk.client;

import com.sec.movietalk.recommendation.dto.MovieRecommendation; // G1
import com.sec.movietalk.recommendation.dto.OnboardingMovie; // G1
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;


@Component
@Getter
public class TmdbClient {

    @Value("${tmdb.api-key}")
    private String apiKey;

    @PostConstruct
    public void init() {
        System.out.println("TMDB API Key = " + apiKey);
    }


    // -------------------G1-------------------//
    public List<OnboardingMovie> fetchOnboardingMovies() {
        // 추후 개발 예정
        return Collections.emptyList();
    }

    public List<MovieRecommendation> fetchRecommendations(Integer movieId) {
        // 추후 개발 예정
        return Collections.emptyList();
    }

    // -------------------G1-------------------//
}


