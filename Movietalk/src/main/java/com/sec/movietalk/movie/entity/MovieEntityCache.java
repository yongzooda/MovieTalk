package com.sec.movietalk.movie.entity;

import jakarta.persistence.*;
        import lombok.*;

        import java.time.LocalDateTime;
import java.time.LocalDate;

@Entity
@Access(AccessType.FIELD)
@Table(name = "movie_cache", indexes = {
        @Index(name = "idx_movie_release", columnList = "release_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieEntityCache {

    @Id
    @Column(name = "movie_id")
    private Integer movieId;

    private String title;

    @Column(name = "poster_url", columnDefinition = "TEXT", insertable = true, updatable = true)
    private String posterUrl;

    @Column(columnDefinition = "TEXT")
    private String overview;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void setCreatedAtNow() {
        this.createdAt = LocalDateTime.now();
    }
}
