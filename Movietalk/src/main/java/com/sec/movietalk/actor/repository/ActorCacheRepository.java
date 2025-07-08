package com.sec.movietalk.actor.repository;

import com.sec.movietalk.common.domain.actor.ActorCache;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActorCacheRepository extends JpaRepository<ActorCache, Long> {
    boolean existsById(Long id);

    List<ActorCache> findByIdIn(List<Integer> actorIds);
}
