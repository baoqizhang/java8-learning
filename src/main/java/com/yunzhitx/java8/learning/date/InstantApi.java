package com.yunzhitx.java8.learning.date;

import java.time.*;
import java.util.Date;

public class InstantApi {

    public static void main(String[] args) {
        // 时间戳的创建
        createInstant();

        // 获取时间戳毫秒数和秒数
        getEpochTime();

        // 与老的时间API互转
        dateConvert();

        // 与其他新日期/时间类的转换
        newDateConvert();
    }

    private static void createInstant() {
        System.out.println("createInstant");

        // 获取当前时间戳
        final Instant now = Instant.now();

        // 从Date获取当前时间戳
        final Instant instantFromDate = new Date().toInstant();
        System.out.println(instantFromDate);

        // 从Clock中获取当前时间戳
        final Clock clock = Clock.systemDefaultZone();
        final Instant instantFromClock = clock.instant();
        System.out.println(instantFromClock);

        // 从带时区的日期时间中获取
        final Instant instantFromZonedDateTime = Instant.from(ZonedDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneId.of("Asia/Shanghai")));
        assert "2019-12-31T16:00:00Z".equals(instantFromZonedDateTime.toString());

        // 从LocalDateTime中获取（需要提供时区）
        final Instant instantFromLocalDateTime = LocalDateTime.of(2020, 1, 1, 0, 0, 0).toInstant(ZoneOffset.ofHours(8));
        assert "2019-12-31T16:00:00Z".equals(instantFromLocalDateTime.toString());
    }

    private static void getEpochTime() {
        System.out.println("getEpochTime");

        final Instant now = Instant.now();
        // 1970-01-01 00:00:00.000到时间戳的毫秒数
        System.out.println(now.toEpochMilli());
        // 1970-01-01 00:00:00到时间戳的秒数
        System.out.println(now.getEpochSecond());
    }

    private static void dateConvert() {
        System.out.println("dateConvert");

        final Instant now = Instant.now();
        // Instant转Dte
        final Date date = Date.from(now);
        // Date转Instant
        final Instant instant = date.toInstant();
    }

    private static void newDateConvert() {
        System.out.println("newDateConvert");

        final Instant now = Instant.now();
        // Instant转LocalDateTime
        final LocalDateTime localDateTime = LocalDateTime.ofInstant(now, ZoneId.systemDefault());
        // Instant转ZonedDateTime
        final ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(now, ZoneId.systemDefault());
    }
}
