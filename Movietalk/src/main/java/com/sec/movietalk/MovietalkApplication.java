package com.sec.movietalk;

import com.sec.movietalk.client.TmdbClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EntityScan(basePackages = {
		"com.sec.movietalk.review.entity",
		"com.sec.movietalk.movie.entity",
		"com.sec.movietalk.common.domain.movie",
		"com.sec.movietalk.common.domain.user",
		"com.sec.movietalk.common.domain.review",   // ✅ 추가된 부분
		"com.sec.movietalk.common.domain.comment",   // ✅ 혹시 comment 엔티티도 쓰면 같이 추가
		"com.sec.movietalk.actor.entity"
})
@EnableJpaRepositories(basePackages = {
		"com.sec.movietalk.review.repository",
		"com.sec.movietalk.movie.repository",
		"com.sec.movietalk.userinfo.repository",
		"com.sec.movietalk.popular.repository",
		"com.sec.movietalk.recommendation.repository",
		"com.sec.movietalk.actor.repository"
})

public class MovietalkApplication {

	public static void main(String[] args) {
		SpringApplication.run(MovietalkApplication.class, args);
	}
}



