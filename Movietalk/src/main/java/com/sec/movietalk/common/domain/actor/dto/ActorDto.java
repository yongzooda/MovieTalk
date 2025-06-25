package com.sec.movietalk.common.domain.actor.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ActorDto {
    private int id; //TMDB 배우 ID
    private String name; // 배우 이름

    @JsonProperty("profile_path")
    private String profilePath;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfilePath() {
        return profilePath;
    }

    public void setProfilePath(String profilePath) {
        this.profilePath = profilePath;
    }

}
