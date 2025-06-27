package com.sec.movietalk.review.entity;

import java.time.LocalDateTime;

public class Review {

    private Long id;
    private Integer movieId;
    private Long userId;
    private String content;
    private int likeCount;
    private int dislikeCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Review() {}

    public Review(Long id, Integer movieId, Long userId, String content,
                  int likeCount, int dislikeCount,
                  LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.movieId = movieId;
        this.userId = userId;
        this.content = content;
        this.likeCount = likeCount;
        this.dislikeCount = dislikeCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Review(Integer movieId, Long userId, String content) {
        this.movieId = movieId;
        this.userId = userId;
        this.content = content;
    }

    public Long getId() { return id; }
    public Integer getMovieId() { return movieId; }
    public Long getUserId() { return userId; }
    public String getContent() { return content; }
    public int getLikeCount() { return likeCount; }
    public int getDislikeCount() { return dislikeCount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setContent(String content) {
        this.content = content;
    }
}


