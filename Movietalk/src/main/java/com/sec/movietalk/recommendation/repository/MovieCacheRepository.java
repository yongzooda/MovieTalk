package com.sec.movietalk.recommendation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.sec.movietalk.common.domain.movie.MovieCache;
public interface MovieCacheRepository extends JpaRepository<MovieCache, Integer> {

    boolean existsByMovieId(Integer movieId);
}
