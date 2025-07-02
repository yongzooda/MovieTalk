package com.sec.movietalk.movie.repository;

import com.sec.movietalk.movie.entity.MovieEntityCache;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<MovieEntityCache, Integer> {

    // ✅ 정렬된 페이징 조회
    Page<MovieEntityCache> findAllByOrderByReleaseDateDesc(Pageable pageable);

    // 💡 필요 시 커스텀 메서드 추가
    // Optional<MovieEntityCache> findByTitle(String title);
}
