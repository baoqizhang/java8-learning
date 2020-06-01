package com.yunzhitx.java8.learning.date;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.ValueRange;

public class LocalDateApi {
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void main(String[] args) {
        // 创建LocalDate
        createLocalDate();

        // LocalDate的计算
        localDateCalculate();
    }

    private static void createLocalDate() {
        System.out.println("createLocalDate");
        // 获取当前日期
        final LocalDate currentDate = LocalDate.now();
        // 获取指定日期 2020年1月2日
        final LocalDate date1 = LocalDate.of(2020, 1, 2);
        // 获取某年的第几天：2020年的第32天（2月1日）
        final LocalDate date2 = LocalDate.ofYearDay(2020, 32);

        // 从LocalDateTime提取
        final LocalDate fromLocalDateTime1 = LocalDate.from(LocalDateTime.now());
        final LocalDate fromLocalDateTime2 = LocalDateTime.now().toLocalDate();

        // 从字符串解析出来
        final LocalDate parsedDate1 = LocalDate.parse("2020-01-01");
        final LocalDate parsedDate2 = LocalDate.parse("2020-01-01", FORMATTER);
    }

    private static void localDateCalculate() {
        System.out.println("localDateCalculate");

        //当前日期加上1天再减去一个月
        final LocalDate date1 = LocalDate.now()
            .plusDays(1)
            .minus(2, ChronoUnit.MONTHS);

        // 判断当前日期所在年份是否是润年
        final boolean leapYear = LocalDate.now().isLeapYear();
        System.out.println(leapYear);

        // isBefore isAfter 判断日期先后
        assert LocalDate.now().isAfter(LocalDate.of(2020, 1, 1));
        assert LocalDate.of(2020, 1, 1).isBefore(LocalDate.now());

        // 转字符串
        assert "2020-01-01".equals(LocalDate.of(2020, 1, 1).format(DateTimeFormatter.ISO_LOCAL_DATE));
        assert "01/01/2020".equals(LocalDate.of(2020, 1, 1).format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));

        // 调整日期
        assert LocalDate.of(2020, 1, 1)
            .with(ChronoField.MONTH_OF_YEAR, 2) // 修改月份为2月
            .with(TemporalAdjusters.next(DayOfWeek.FRIDAY)) // 下个周五
            .with(TemporalAdjusters.lastDayOfMonth()) // 当月的最后一天
            .equals(LocalDate.of(2020, 2, 29));

        // 获取某个字段的取值范围，上面的TemporalAdjusters.lastDayOfMonth()就是用到了这个range方法
        // 比如2020年2月的日期范围
        final ValueRange range = LocalDate.of(2020, 2, 1).range(ChronoField.DAY_OF_MONTH);
        // 2020年的2月的日期，最大值是29，因为润年有29天
        assert 29 == range.getMaximum();
        // 2020年2月的日期，最小值是1
        assert 1 == range.getMinimum();

        // 转换成LocalDateTime
        final LocalDateTime localDateTime = LocalDate.now().atStartOfDay();
        final LocalDateTime localDateTime1 = LocalDate.now().atTime(8, 30, 5);

        // 转换成带有时区信息的ZonedDateTime
        final ZonedDateTime zonedDateTime = LocalDate.now().atStartOfDay(ZoneId.systemDefault());
        final ZonedDateTime zonedDateTime1 = LocalDate.now()
            .atTime(LocalTime.of(8, 30, 5))
            .atZone(ZoneId.systemDefault());

    }
}
