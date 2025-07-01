package com.sec.movietalk.recommendation.service;

import com.sec.movietalk.client.TmdbClient;
import com.sec.movietalk.common.domain.movie.MovieCache;
import com.sec.movietalk.common.domain.user.UserFavorite;
import com.sec.movietalk.common.domain.user.User;
import com.sec.movietalk.common.domain.user.UserFavorite;

import com.sec.movietalk.recommendation.dto.*;
import com.sec.movietalk.recommendation.repository.UserFavoriteRepository;
import com.sec.movietalk.recommendation.repository.MovieCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private final TmdbClient tmdbClient;
    private final MovieCacheRepository movieCacheRepository;
    private final UserFavoriteRepository userFavoriteRepository;

    public RecommendationService(TmdbClient tmdbClient,
                                 MovieCacheRepository movieCacheRepository,
                                 UserFavoriteRepository userFavoriteRepository) {
        this.tmdbClient = tmdbClient;
        this.movieCacheRepository = movieCacheRepository;
        this.userFavoriteRepository = userFavoriteRepository;
    }
    /*
        1. 유저의 선호 영화 >= 3 확인
     */
    public CheckFavoriteRes hasFavorite(Long userID){
        long count = userFavoriteRepository.countByUser_UserId(userID);
        return new CheckFavoriteRes(count >=3);
    }


    //    ------- 2. 온보딩용 영화 후보 20편 ------
    public List<OnboardingMovie> getOnboardingMovies(){
        return tmdbClient.fetchOnboardingMovies();
    }

    //    ------- 3. 선택된 영화들 UserFavorite에 저장 ------
    public void saveFavorites(Long userId, List<Long> movieIds){
        User userRef = new User();
        userRef.setUserId(userId);

        List<UserFavorite> insertList = movieIds.stream()
                .filter(id -> !userFavoriteRepository.existsByUser_UserIdAndMovieId(userId, id.intValue()))
                .map(id -> new UserFavorite(userRef, id.intValue()))  // 2-arg 생성자 사용
                .collect(Collectors.toList());

        userFavoriteRepository.saveAll(insertList);
    }

    //    ------- 4. TMDB 추천 합집합 생성 + 필터링 + 정렬 ------

}
