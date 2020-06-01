package com.yunzhitx.java8.learning.date;


import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;

public class LocalTimeApi {
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static void main(String[] args) {
        // 创建LocalTime
        createLocalTime();

        // LocalTime计算
        localTimeCalculate();
    }

    private static void createLocalTime() {
        // 当前时间
        LocalTime.now();
        // 时分 01:02
        LocalTime.of(1, 2);
        // 时分秒 01:02:03
        LocalTime.of(1, 2, 3);
        // 时分秒纳秒 01:02:03.000000800
        LocalTime.of(1, 2, 3, 800);
        // 一天的第300秒的时间
        LocalTime.ofSecondOfDay(300);
        // 从LocalDateTime提取时间
        LocalTime.from(LocalDateTime.now());
        LocalDateTime.now().toLocalDate();
        // 从字符串解析
        LocalTime.parse("08:00:00");
        LocalTime.parse("08:00:00", TIME_FORMATTER);
    }

    private static void localTimeCalculate() {
        final LocalTime time = LocalTime.of(8, 0, 0);

        assert "08:30:00".equals(
            time.plusHours(1) // 增加一个小时
                .minus(30, ChronoUnit.MINUTES) // 减30分钟
                .format(TIME_FORMATTER) // 按照指定格式，格式化成字符串
        );

        // 判断时间先后
        assert time.isAfter(LocalTime.of(7, 59, 59));
        assert LocalTime.of(7, 59, 59).isBefore(time);

        assert 40 == time.with(ChronoField.MINUTE_OF_HOUR, 20) // 分钟调整为20分
            .withHour(9) // 小时调整为9点
            .until(LocalTime.of(10, 0, 0), ChronoUnit.MINUTES); // 等多少分钟到10点

        // 计算时间差
        assert 1 == Duration.between(time, LocalTime.of(9, 0, 0)).toHours();

        // 与LocalDateTime和ZonedDateTime的转换
        final ZonedDateTime zonedDateTime = time
            .atDate(LocalDate.of(2020, 1, 1)) // 加上LocalDate，转换成LocalDateTime
            .atZone(ZoneId.systemDefault());// 再加上时区，转换成ZonedDateTime
    }
}
