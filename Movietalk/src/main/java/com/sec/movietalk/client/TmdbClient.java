package com.sec.movietalk.client;

import com.sec.movietalk.common.domain.actor.dto.ActorDto;
import com.sec.movietalk.common.domain.actor.dto.TmdbActorSearchResponse;
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


}


