package com.yunzhitx.java8.learning.date;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class LocalDateTimeApi {
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public static void main(String[] args) {
        createLocalDateTime();

        localDateTimeCalculate();
    }

    private static void createLocalDateTime() {
        // 当前日期时间
        LocalDateTime.now();
        // 获取当前时间对应的UTC 0时区的时间
        LocalDateTime.now(ZoneOffset.UTC);
        // 根据时间戳和时区，得到LocalDateTime的表示（这里会比当前时间小8小时）
        LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);
        // 通过年月日时分秒创建 2020-01-01 08:00:00
        LocalDateTime.of(2020, 1, 1, 8, 0, 0);
        // 通过LocalDate和LocalTime组合
        LocalDateTime.of(LocalDate.now(), LocalTime.now());
        //从字符串解析
        LocalDateTime.parse("2020-01-01T08:00:00");
        // 使用自定义DateTimeFormatter格式化
        LocalDateTime.parse("2020-01-01T08:00:00", DATE_TIME_FORMATTER);
        // 使用内置DateTimeFormatter格式化
        LocalDateTime.parse("2020-01-01T08:00:00", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    private static void localDateTimeCalculate() {
        final LocalDateTime dateTime = LocalDateTime.of(2020, 1, 1, 8, 0, 0);
        // 提取日期部分到LocalDate
        final LocalDate localDate = dateTime.toLocalDate();
        // 提取时间部分到LocalTime
        final LocalTime localTime = dateTime.toLocalTime();
        // 转换成时间戳
        final Instant instant = dateTime.atZone(ZoneId.systemDefault()).toInstant();

        // 日期时间加减
        assert "2021-02-02T08:59:01".equals(
            dateTime.plusYears(1)
                .plusMonths(1)
                .plusDays(1)
                .plusHours(1)
                .minusMinutes(1)
                .plusSeconds(1)
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );

        // 计算到某个时间还有多少小时
        assert 1 == dateTime.until(
            LocalDateTime.of(2020, 1, 1, 9, 0, 0),
            ChronoUnit.HOURS
        );

        // 截断到天，时分秒都变为0
        assert "2020-01-01T00:00:00".equals(
            dateTime.truncatedTo(ChronoUnit.DAYS)
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );

        // 转换为带时区的对象
        final ZonedDateTime zonedDateTime = dateTime.atZone(ZoneId.systemDefault());
        // 转换为某个时区的Date
        final Date date = Date.from(
            dateTime.atZone(ZoneId.systemDefault()).toInstant()
        );
    }
}
