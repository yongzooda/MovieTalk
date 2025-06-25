package com.sec.movietalk.common.domain.actor.controller;

import com.sec.movietalk.client.TmdbClient;
import com.sec.movietalk.common.domain.actor.dto.ActorDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/actors")
public class ActorController {


    private final TmdbClient tmdbClient; //TMDB API와 통신하기 위해 가져온 클라이언트 객체

    // tmdbClient를 스프링이 자동으로 주입할 수 있도록 만든 생성자 기반 의존성 주입
    public ActorController(TmdbClient tmdbClient) {
        this.tmdbClient = tmdbClient;
    }

    @GetMapping("/search")
    public String search(@RequestParam String query, Model model) {
        // tmdbClient를 통해 query로 배우 검색을 하고, 결과를 ActorDto 리스트로 받아옴
        List<ActorDto> actors = tmdbClient.searchActors(query);
        // actors라는 이름으로 모델에 배우 목록을 담음
        // 타임리프 템플릿 search.html에서 ${actors}로 접근 가능
        model.addAttribute("actors", actors);

        return "actor/search";
    }
}
