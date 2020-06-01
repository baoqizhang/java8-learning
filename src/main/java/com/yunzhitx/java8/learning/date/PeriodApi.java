package com.yunzhitx.java8.learning.date;

import java.time.LocalDate;
import java.time.Period;

public class PeriodApi {

    public static void main(String[] args) {
        // 创建Period
        createPeriod();
        // Period转换
        periodTransform();
    }

    private static void createPeriod() {
        // 创建1年2月3天的日期间隔
        final Period period1 = Period.of(1, 2, 3);
        // 创建30天的日期间隔
        final Period period2 = Period.ofDays(30);
        // 根据起止日期计算间隔
        final Period period3 = Period.between(
            LocalDate.of(2020, 1, 14),
            LocalDate.of(2020, 1, 1)
        );
    }

    private static void periodTransform() {
        // 创建1年+2个月+3天的日期间隔
        final Period period = Period.ofYears(1).plusMonths(2).plusDays(3);
        // 两个Period计算：1个月减15天
        final Period period2 = Period.ofMonths(1).minus(Period.ofDays(15));
    }
}
