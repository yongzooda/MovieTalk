package com.sec.movietalk.common.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * movieCache ‧ creditsCache 등 이름별 캐시를 관리
     * - 최대 5,000 엔트리
     * - 마지막 접근 기준 24h 후 만료
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager(
                "movieCache", "creditsCache", "trendingCache");
        manager.setCaffeine(
                Caffeine.newBuilder()
                        .maximumSize(5_000)
                        .expireAfterAccess(Duration.ofHours(24))
        );
        return manager;
    }
}
