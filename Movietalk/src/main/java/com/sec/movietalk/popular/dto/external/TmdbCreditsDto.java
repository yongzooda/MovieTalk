package com.sec.movietalk.popular.dto.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class TmdbCreditsDto {
    private int id;
    private List<Cast> cast;

    @Data
    public static class Cast {
        private int    id;
        private String name;
        @JsonProperty("profile_path") private String profilePath;
        private String character;
    }
}