package com.yunzhitx.java8.learning.date;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;

public class ClockApi {

    public static void main(String[] args) {
        // 创建始终
        createClock();
        // 获取当前时间戳
        getTimeFromClock();
        // 时钟变换
        clockTransform();
    }

    private static void createClock() {
        System.out.println("createClock");
        // 获取系统时区的钟
        final Clock clockOfSystemDefaultZone = Clock.systemDefaultZone();
        // 获取0时区的钟
        final Clock clockOfUTCTimeZone = Clock.systemUTC();
        // 获取指定时区的钟
        final Clock clockOfSpecifyZone = Clock.system(ZoneId.of("Asia/Shanghai"));
    }

    private static void getTimeFromClock() {
        final Clock clock = Clock.systemDefaultZone();
        // 获取当前时间戳
        final Instant instant = clock.instant();
        System.out.println(instant);
    }

    private static void clockTransform() {
        System.out.println("createClock");
        // 获取系统时区的钟
        final Clock sysClock = Clock.systemDefaultZone();
        // 获取比系统时区快一个小时的钟
        final Clock offsetClock = Clock.offset(sysClock, Duration.ofHours(1));
        System.out.println(sysClock.instant());
        System.out.println(offsetClock.instant());

        // 系统时钟秒针滴答5次，他的秒针才滴答一次，但是一次滴答就前进5秒（第二个参数定义每次秒针滴答的间隔）
        final Clock tickClock = Clock.tick(sysClock, Duration.ofSeconds(5));
        try {
            for (int i=0; i < 16; i++) {
                System.out.println("sysClock: " + sysClock.instant());
                System.out.println("tickClock：" + tickClock.instant());
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
