package com.sec.movietalk.client;


import com.sec.movietalk.actor.dto.ActorDto;
import com.sec.movietalk.actor.dto.TmdbActorSearchResponse;
import com.sec.movietalk.actor.external.TmdbMovieCreditsResponse;
import com.sec.movietalk.recommendation.dto.MovieRecommendation; // G1
import com.sec.movietalk.recommendation.dto.OnboardingMovie; // G1
import java.util.Collections; // G1
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
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



    private final RestTemplate restTemplate = new RestTemplate();

    public List<ActorDto> searchActors(String query) {
        String url = "https://api.themoviedb.org/3/search/person?api_key=" + apiKey +
                "&language=ko-KR&query=" + UriUtils.encode(query, StandardCharsets.UTF_8);

        ResponseEntity<TmdbActorSearchResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        return response.getBody().getResults();
    }

    public ActorDto getActorDetail(int id) {
        String url = "https://api.themoviedb.org/3/person/" + id + "?api_key=" + apiKey + "&language=ko-KR";

        ResponseEntity<ActorDto> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<ActorDto>() {}
        );

        return response.getBody();
    }

    public TmdbMovieCreditsResponse getMovieCredits(Long personId, String apiKey) {
        String url = "https://api.themoviedb.org/3/person/" + personId + "/movie_credits?api_key=" + apiKey + "&language=ko-KR";

        ResponseEntity<TmdbMovieCreditsResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                TmdbMovieCreditsResponse.class
        );

        return response.getBody();
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


