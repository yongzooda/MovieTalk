package com.sec.movietalk.popular.repository.projection;

/** 리뷰 수 집계 결과 */
public interface ReviewCountProjection {
    int  getMovieId();
    long getCnt();
}


