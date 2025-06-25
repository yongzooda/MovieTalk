package com.sec.movietalk.popular.dto.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TmdbMovieDto {
    private int id;
    private String title;
    @JsonProperty("poster_path")  private String posterPath;
    private String overview;
    @JsonProperty("release_date") private LocalDate releaseDate;
}