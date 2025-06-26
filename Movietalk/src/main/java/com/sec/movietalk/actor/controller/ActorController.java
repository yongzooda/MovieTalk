package com.sec.movietalk.actor.controller;

import com.sec.movietalk.actor.dto.ActorDto;
import com.sec.movietalk.client.TmdbClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    // 배우 검색
    @GetMapping("/search")
    public String search(@RequestParam(value = "query", required = false) String query, Model model) {

        // 사용자가 입력한 검색어가 NUll이 아니고 공백이 아니면 TMDB API에 검색 요청
        if (query != null && !query.isBlank()) {
            List<ActorDto> actors = tmdbClient.searchActors(query);
            model.addAttribute("actors", actors);
            model.addAttribute("query", query);
        }

        return "actor/search";

    }

    // 배우 상세 정보
    @GetMapping("/{id}")
    public String actorDetail(@PathVariable("id") int id, Model model) {

        ActorDto actor = tmdbClient.getActorDetail(id); // 배우 상세 정보를 TMDB API에서 가져옴
        model.addAttribute("actor", actor);

        return "actor/detail";
    }
}
