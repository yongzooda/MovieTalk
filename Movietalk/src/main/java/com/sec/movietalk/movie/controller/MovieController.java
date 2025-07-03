package com.sec.movietalk.movie.controller;

import com.sec.movietalk.client.TmdbClient;
import com.sec.movietalk.common.domain.movie.MovieCache;
import com.sec.movietalk.movie.dto.MovieDetailDto;
import com.sec.movietalk.movie.dto.MovieResponseDto;
import com.sec.movietalk.movie.dto.MovieSearchResultDto;
import com.sec.movietalk.movie.service.MovieCacheService;
import com.sec.movietalk.movie.service.MovieService;
import com.sec.movietalk.movie.service.MovieViewService;
import com.sec.movietalk.common.util.UserUtil; // ✅ 유저 아이디 추출 유틸 추가
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal; // ✅ import 추가
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;
    private final TmdbClient tmdbClient;
    private final MovieCacheService movieCacheService;
    private final MovieViewService movieViewService;

    @GetMapping("/movies")
    public String showMovieList(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<MovieResponseDto> movies = movieService.getPagedMovies(page);
        model.addAttribute("movies", movies.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", movies.getTotalPages());
        return "movie/list";
    }

    @GetMapping("/movies/{id}")
    public String getMovieDetail(@PathVariable Integer id,
                                 @AuthenticationPrincipal Object principal, // ✅ 로그인 유저 정보 주입
                                 Model model) {

        Long userId = UserUtil.extractUserId(principal); // ✅ 로그인한 유저 ID 추출
        movieViewService.recordView(id, userId);         // ✅ ID 전달

        Optional<MovieCache> movieOpt = movieService.findMovieEntityById(id);

        if (movieOpt.isPresent()) {
            MovieCache movie = movieOpt.get();
            MovieResponseDto movieDto = MovieResponseDto.fromEntity(movie);
            MovieDetailDto detail = movieService.getMovieDetailFromTmdb(movie.getMovieId());

            if (detail != null && detail.isAdult()) {
                model.addAttribute("adultRestricted", true);
                return "movie/detail";
            }

            model.addAttribute("movie", movieDto);
            model.addAttribute("detail", detail);
            model.addAttribute("adultRestricted", false);
            return "movie/detail";

        } else {
            MovieDetailDto detail = movieService.getMovieDetailFromTmdbId(id);

            if (detail != null && detail.isAdult()) {
                model.addAttribute("adultRestricted", true);
                return "movie/detail";
            }

            movieCacheService.saveIfNotExists(detail);

            model.addAttribute("detail", detail);
            model.addAttribute("adultRestricted", false);
            return "movie/detail";
        }
    }

    @GetMapping("/movies/search")
    public String searchMovies(@RequestParam String keyword,
                               @RequestParam(defaultValue = "0") int page,
                               Model model) {
        Page<MovieSearchResultDto> results = movieService.searchMoviesFromTmdb(keyword, page);
        int resultCount = movieService.getSearchResultCount(keyword);

        model.addAttribute("movies", results.getContent());
        model.addAttribute("keyword", keyword);
        model.addAttribute("resultCount", resultCount);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", results.getTotalPages());
        return "movie/list";
    }
}
