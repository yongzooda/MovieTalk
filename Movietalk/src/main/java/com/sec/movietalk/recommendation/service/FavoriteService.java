package com.sec.movietalk.recommendation.service;

import com.sec.movietalk.common.domain.movie.MovieCache;
import com.sec.movietalk.common.domain.user.User;
import com.sec.movietalk.common.domain.user.UserFavorite;
import com.sec.movietalk.home.dto.HomeMovieDto;
import com.sec.movietalk.recommendation.repository.MovieCacheRepository;
import com.sec.movietalk.recommendation.repository.UserFavoriteRepository;
import com.sec.movietalk.userinfo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FavoriteService {

    private final UserFavoriteRepository favoriteRepo;
    private final UserRepository userRepo;
    private final MovieCacheRepository movieRepo;   // 포스터·제목 캐싱용

    // 즐겨찾기 목록
    @Transactional(readOnly = true)
    public List<HomeMovieDto> getFavorites(Long userId) {   // HomeMovieDto 재사용
        return favoriteRepo.findAllByUser_UserId(userId).stream()
                .map(fav -> {
                    MovieCache m = movieRepo.findById(fav.getMovieId()).orElseThrow();
                    return new HomeMovieDto(m.getMovieId(), m.getTitle(), m.getPosterUrl());
                })
                .toList();
    }

    // 추가
    public void addFavorite(Long userId, Integer movieId) {
        if (favoriteRepo.existsByUser_UserIdAndMovieId(userId, movieId)) return;
        User user = userRepo.findById(userId).orElseThrow();
        favoriteRepo.save(new UserFavorite(user, movieId));
    }

    // 삭제
    public void removeFavorite(Long userId, Integer movieId) {
        favoriteRepo.deleteByUser_UserIdAndMovieId(userId, movieId);
    }
}

