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
    private Long id;

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

    // ✅ ReviewService에서 사용되는 생성자
    public Review(Integer movieId, Long userId, String content) {
        this.movieId = movieId;
        this.user = new User(userId); // 단일 userId만 세팅하는 생성자 필요
        this.content = content;
    }

    // ✅ 사용자 ID를 반환하는 getter
    public Long getUserId() {
        return user != null ? user.getId() : null;
    }

    // ✅ 리뷰 내용 수정용 setter
    public void setContent(String content) {
        this.content = content;
    }

    // ✅ 좋아요/싫어요 getter (lombok 미적용 대비)
    public int getLikeCount() {
        return likeCount;
    }

    public int getDislikeCount() {
        return dislikeCount;
    }
}



