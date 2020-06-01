package com.yunzhitx.java8.learning.date;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Set;

/**
 * 时区相关API
 */
public class ZoneApi {

    public static void main(String[] args) {
        // 获取ZoneId
        getZoneId();

        // 获取ZoneOffset
        getZoneOffset();
    }

    public static void getZoneId() {
        // 获取系统时区
        final ZoneId systemDefaultZoneId = ZoneId.systemDefault();
        final ZoneId systemDefaultZoneId2 = ZoneOffset.systemDefault();

        // 获取指定ID的时区
        final ZoneId specifyZoneId = ZoneId.of("Asia/Shanghai");
        // 获取所有内置ZoneId字符串
        final Set<String> availableZoneIds = ZoneId.getAvailableZoneIds();
        // 从ZoneOffset获取ZoneId
        ZoneOffset.ofHours(8); // 东八区
        ZoneOffset.ofOffset("UTC", ZoneOffset.ofHours(8));
    }

    public static void getZoneOffset() {
        // 0时区
        final ZoneOffset utc = ZoneOffset.UTC;
        // 根据数字小时 +08:00偏移量
        final ZoneOffset zoneOffset = ZoneOffset.ofHours(8);
        // 根据时间分钟 +08:30偏移量
        final ZoneOffset zoneOffset2 = ZoneOffset.ofHoursMinutes(8, 30);
        // 根据字符串 +08:00偏移量
        final ZoneOffset zoneOffset3 = ZoneOffset.of("+08:00");
    }
}
