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
public class MyCommentResponseDto {

    private Long commentId;
    private String content;
    private LocalDateTime createdAt;
    private String dislikeCnt;
    private String likeCnt;
    private String reviewId;

}
