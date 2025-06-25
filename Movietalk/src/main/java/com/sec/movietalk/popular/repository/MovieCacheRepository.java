// com/sec/movietalk/popular/repository/MovieCacheRepository.java
package com.sec.movietalk.popular.repository;

import com.sec.movietalk.common.domain.movie.MovieCache;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieCacheRepository extends JpaRepository<MovieCache, Integer> {
}
