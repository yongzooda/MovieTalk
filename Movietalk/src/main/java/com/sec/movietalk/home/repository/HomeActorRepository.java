package com.sec.movietalk.home.repository;

import com.sec.movietalk.common.domain.comment.ActorComment;
import com.sec.movietalk.common.domain.review.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HomeActorRepository extends JpaRepository<ActorComment, Long> {

    @Query("""
        SELECT ac.actorId AS actorId, COUNT(ac) AS cnt
        FROM ActorComment ac
        WHERE ac.actorId IS NOT NULL
        GROUP BY ac.actorId
        ORDER BY COUNT(ac) DESC
""")
    Page<ActorCommentCountProjection> findTop4ByActorIdCount(Pageable pageable);

    interface ActorCommentCountProjection {
        Long getActorId();
        Long getCnt();
    }

}
