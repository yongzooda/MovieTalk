package com.sec.movietalk.home.service;

import com.sec.movietalk.common.domain.movie.MovieCache;
import com.sec.movietalk.home.dto.HomeMovieDto;
import com.sec.movietalk.home.repository.HomeMovieRepository;
import com.sec.movietalk.home.repository.MovieCache3Repository;
import com.sec.movietalk.recommendation.repository.MovieCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeMovieService {

    private final HomeMovieRepository movieViewsRepo;
    private final MovieCache3Repository movieCacheRepo;

    public List<HomeMovieDto> getTop4MoviesByViews() {

        Pageable pageable = PageRequest.of(0, 4);
        Page<HomeMovieRepository.MovieViewsCountProjection> top4Page = movieViewsRepo.findTop4ByMovieIdCount(pageable);

        List<HomeMovieDto> result = new ArrayList<>();
        for (var proj : top4Page.getContent()) {
            Integer movieId = proj.getMovieId();
            MovieCache movie = movieCacheRepo.findById(movieId).orElse(null);
            if (movie != null) {
                result.add(new HomeMovieDto(
                        movieId,
                        movie.getTitle(),
                        movie.getPosterUrl()
                ));
            }
        }
        return result;
    }
}
