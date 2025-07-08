package com.sec.movietalk.userinfo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MyActorCommentResponseDto {

    private Long actorCommentId;
    private Long actorId;
    private String actorContent;
    private String actorName;
    private LocalDateTime createdAt;


}
