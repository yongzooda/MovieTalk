package com.sec.movietalk.movie.controller;

import com.sec.movietalk.client.TmdbClient;
import com.sec.movietalk.common.domain.movie.MovieCache;
import com.sec.movietalk.common.util.UserUtil;
import com.sec.movietalk.movie.dto.MovieDetailDto;
import com.sec.movietalk.movie.dto.MovieResponseDto;
import com.sec.movietalk.movie.dto.MovieSearchResultDto;
import com.sec.movietalk.movie.mapper.GenreMapper;
import com.sec.movietalk.movie.service.MovieCacheService;
import com.sec.movietalk.movie.service.MovieService;
import com.sec.movietalk.movie.service.MovieViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;
    private final TmdbClient tmdbClient;
    private final MovieCacheService movieCacheService;
    private final MovieViewService movieViewService;

    private void addPaginationInfo(Model model, int page, int totalPages) {
        int blockSize = 10;
        int currentBlock = page / blockSize;
        int startPage = currentBlock * blockSize;
        int endPage = Math.min(totalPages - 1, startPage + blockSize - 1);

        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
    }

    @GetMapping("/movies")
    public String showMovieList(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<MovieResponseDto> movies = movieService.getPagedMovies(page);

        model.addAttribute("movies", movies.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", movies.getTotalPages());

        addPaginationInfo(model, page, movies.getTotalPages());

        return "movie/list";
    }

    @GetMapping("/movies/{id}")
    public String getMovieDetail(@PathVariable Integer id,
                                 @RequestParam(required = false) String from,
                                 @RequestParam(required = false) String keyword,
                                 @RequestParam(required = false) String category,
                                 @RequestParam(required = false) Integer page,
                                 @AuthenticationPrincipal Object principal,
                                 Model model) {

        Long userId = UserUtil.extractUserId(principal);
        movieViewService.recordView(id, userId);

        model.addAttribute("from", from);
        model.addAttribute("keyword", keyword);
        model.addAttribute("page", page);
        model.addAttribute("category", category);

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
                               @RequestParam(defaultValue = "title") String category,
                               @RequestParam(defaultValue = "0") int page,
                               Model model) {

        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", page);
        model.addAttribute("category", category);

        if ("genre".equals(category)) {
            String genreId = GenreMapper.getGenreId(keyword.trim());

            if (genreId == null) {
                model.addAttribute("movies", List.of());
                model.addAttribute("resultCount", 0);
                model.addAttribute("totalPages", 0);
                model.addAttribute("startPage", 0);
                model.addAttribute("endPage", 0);
                return "movie/list";
            }

            Page<MovieSearchResultDto> results = movieService.searchMoviesByGenreId(genreId, page);

            model.addAttribute("movies", results.getContent());
            model.addAttribute("resultCount", results.getTotalElements());
            model.addAttribute("totalPages", results.getTotalPages());

            addPaginationInfo(model, page, results.getTotalPages());

            return "movie/list";
        }

        // 기본: 제목 검색
        Page<MovieSearchResultDto> results = movieService.searchMoviesFromTmdb(keyword, page);

        model.addAttribute("movies", results.getContent());
        model.addAttribute("resultCount", results.getTotalElements());
        model.addAttribute("totalPages", results.getTotalPages());

        addPaginationInfo(model, page, results.getTotalPages());

        return "movie/list";
    }
}
