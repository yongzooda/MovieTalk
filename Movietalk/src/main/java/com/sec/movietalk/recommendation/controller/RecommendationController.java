package com.sec.movietalk.recommendation.controller;


import com.sec.movietalk.recommendation.dto.CheckFavoriteRes;
import com.sec.movietalk.recommendation.dto.OnboardingMovie;
import com.sec.movietalk.recommendation.dto.FavoriteSaveReq;
import com.sec.movietalk.recommendation.dto.MovieRecommendation;
import com.sec.movietalk.recommendation.dto.RecommendationRes;
import com.sec.movietalk.recommendation.service.RecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final RecommendationService service;

    public RecommendationController(RecommendationService service) {
        this.service = service;
    }

    // 1: 선호 영화 => 3인지 확인
    @GetMapping("/check/{user_id}")
    public CheckFavoriteRes checkFavorite(@PathVariable("user_id") Long userId) {
        return service.hasFavorite(userId);
    }

    // 2 온보딩용 후보 영화 20편
    @GetMapping("/onboarding/movies")
    public List<OnboardingMovie> getOnboardingMovies() {
        return service.getOnboardingMovies();
    }

    // 3: 온보딩 완료 → 선호 영화 저장
    @PostMapping("/onboarding/complete/{user_id}")
    public ResponseEntity<Void> completeOnboarding(
            @PathVariable("user_id") Long userId,
            @RequestBody FavoriteSaveReq req
    ) {
        service.saveFavorites(userId, req.getMovie_ids());
        return ResponseEntity.ok().build();
    }

    // 4 유저별 추천 리스트 반환
}
