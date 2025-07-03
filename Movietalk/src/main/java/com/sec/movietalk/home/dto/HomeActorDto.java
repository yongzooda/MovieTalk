package com.sec.movietalk.home.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HomeActorDto {

    private Integer actorId;
    private String name;
    private String profilePath;
}
