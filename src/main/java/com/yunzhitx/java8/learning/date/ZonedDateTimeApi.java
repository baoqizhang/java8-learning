package com.yunzhitx.java8.learning.date;

import java.sql.Date;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class ZonedDateTimeApi {
    public static void main(String[] args) {
        // 创建ZonedDateTime
        createZonedDateTime();
        // ZonedDateTime的计算
        zonedDateTimeCalculate();
    }

    private static void createZonedDateTime() {
        // 当前时区的当前时间
        ZonedDateTime.now();
        // 零时区的当前时间
        ZonedDateTime.now(ZoneOffset.UTC);
        // 从LocalDateTime和时区创建
        ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault());
        // 从LocalDate，LocalTime和时区创建
        ZonedDateTime.of(LocalDate.now(), LocalTime.now(), ZoneId.systemDefault());
        // 从年月日时分秒纳秒和时区创建
        ZonedDateTime.of(2020, 1, 1, 8, 0, 0, 0, ZoneId.systemDefault());
    }

    private static void zonedDateTimeCalculate() {
        final ZonedDateTime zonedDateTime = ZonedDateTime.of(2020, 1, 1, 8, 0, 0, 0, ZoneId.systemDefault());
        // 转换成时间戳
        zonedDateTime.toInstant();
        // 转换成Date
        Date.from(zonedDateTime.toInstant());
        // 加减计算
        zonedDateTime.plusDays(30)
            .minus(1, ChronoUnit.DAYS)
            .plusMonths(1);
        // 格式化成字符串
        zonedDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        // 提取LocalDateTime，LocalDate, LocalTime和时区
        final LocalDateTime localDateTime = zonedDateTime.toLocalDateTime();
        final LocalDate localDate = zonedDateTime.toLocalDate();
        final LocalTime localTime = zonedDateTime.toLocalTime();
        final ZoneId zone = zonedDateTime.getZone();

        // 时间表示不变，只切换时区，把东八区的1月1号8点变成零时区的1月1号8点
        assert "2020-01-01T08:00Z".equals(zonedDateTime.withZoneSameLocal(ZoneOffset.UTC).toString());
        // 时间戳不变，改时区，同一个时间戳在不同时区的时间是不痛的，因此，会有时区转换，东八区的时间戳在零时区要少8个小时
        assert "2020-01-01T00:00Z".equals(zonedDateTime.withZoneSameInstant(ZoneOffset.UTC).toString());
    }
}
