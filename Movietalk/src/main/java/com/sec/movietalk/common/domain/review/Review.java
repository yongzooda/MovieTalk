package com.sec.movietalk.common.domain.review;

import com.sec.movietalk.common.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "review")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewId;

    private Integer movieId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private int likeCount = 0;
    private int dislikeCount = 0;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Review(Integer movieId, Long userId, String content) {
        this.movieId = movieId;
        this.user = new User(userId);
        this.content = content;
    }

    public Long getUserId() {
        return user != null ? user.getId() : null;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public int getDislikeCount() {
        return dislikeCount;
    }

    public Long getId() {
        return this.reviewId;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}






