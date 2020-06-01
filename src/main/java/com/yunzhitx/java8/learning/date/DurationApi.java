package com.yunzhitx.java8.learning.date;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;

public class DurationApi {

    public static void main(String[] args) {
        // 创建Duration
        createDuration();

        // Duration变换
        durationTransform();
    }

    private static void createDuration() {
        // 创建1小时的间隔
        final Duration oneHour = Duration.ofHours(1);

        // 创建3天的间隔
        final Duration threeDays = Duration.ofDays(3);

        // 创建一分钟的间隔
        final Duration oneMinutes = Duration.of(1, ChronoUnit.MINUTES);

        // 根据起始时间计算时间间隔
        final Duration towTimeDuration = Duration.between(
            LocalDateTime.of(2020, 1, 1, 8, 0, 0),
            LocalDateTime.of(2020, 1, 1, 10, 0, 0)
        );
        assert 2 == towTimeDuration.toHours();
    }

    private static void durationTransform() {
        // 获取1小时+30分钟-20秒的时间间隔
        final Duration duration = Duration.ofHours(1)
            .plus(30, ChronoUnit.MINUTES)
            .minus(20, ChronoUnit.SECONDS);

        // 时间间隔转换成分钟数
        final long minutes = duration.toMinutes();
        // 时间间隔转换成纳秒
        final long nanos = duration.toNanos();
    }
}
