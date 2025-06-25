package com.sec.movietalk.popular.repository.projection;

/** 조회 수 집계 결과 */
public interface ViewCountProjection {
    int  getMovieId();
    long getCnt();
}