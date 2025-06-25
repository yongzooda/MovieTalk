package com.sec.movietalk.common.domain.movie;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "movie_view_daily")
@IdClass(MovieViewDailyId.class)            // ★ 핵심
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class MovieViewDaily {

    @Id
    @Column(name = "movie_id", nullable = false)
    private Integer movieId;

    @Id
    @Column(name = "view_date", nullable = false)
    private LocalDate viewDate;

    @Column(nullable = false, columnDefinition = "int default 0")
    private Integer cnt;
}
