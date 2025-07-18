package com.sec.movietalk.common.domain.review;

import com.sec.movietalk.common.domain.comment.Comment;
import com.sec.movietalk.common.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "review", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "review", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ReviewReports> reports = new ArrayList<>();

    @OneToMany(mappedBy = "review", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ReviewReactions> reactions = new ArrayList<>();

    @OneToMany(mappedBy = "review", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ReviewViews> views = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private int likeCount = 0;
    private int dislikeCount = 0;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private Double rating;

    // ---- 생성자/세터/게터/기타 메서드 ----

    public Review(Integer movieId, Long userId, String content, Double rating) {
        this.movieId = movieId;
        this.user = new User(userId);
        this.content = content;
        this.rating = rating;
    }

    public Long getUserId() {
        return user != null ? user.getId() : null;
    }

    public void setContent(String content) {
        this.content = content;
    }
    public void setRating(Double rating) { this.rating = rating; }
    public Double getRating() { return this.rating; }

    public int getLikeCount() { return likeCount; }
    public int getDislikeCount() { return dislikeCount; }
    public Long getId() { return this.reviewId; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public void setLikeCount(int likeCount) { this.likeCount = likeCount; }
    public void setDislikeCount(int dislikeCount) { this.dislikeCount = dislikeCount; }
}
