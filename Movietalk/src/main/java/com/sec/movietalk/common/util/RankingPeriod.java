package com.sec.movietalk.common.util;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 기간별 랭킹(일/주/월/전체) 계산용 Enum
 */
public enum RankingPeriod {

    DAILY(1),
    WEEKLY(7),
    MONTHLY(30),
    ALLTIME(Integer.MAX_VALUE);

    private final int days;

    RankingPeriod(int days) {
        this.days = days;
    }

    public int getDays() {
        return days;
    }

    /** 기준일(now)로부터 from ~ to 범위 반환 */
    public LocalDateTime getFrom(LocalDateTime now) {
        return (this == ALLTIME) ? LocalDateTime.MIN : now.minusDays(days);
    }

    public static RankingPeriod of(String val) {
        if (val == null) return DAILY;
        try {
            return RankingPeriod.valueOf(val.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return DAILY;
        }
    }
}
