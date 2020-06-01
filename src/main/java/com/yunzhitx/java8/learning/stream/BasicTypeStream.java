package com.yunzhitx.java8.learning.stream;

import java.util.IntSummaryStatistics;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * ClassName: 基础类型流 <br/>
 * Description: <br/>
 * date: 2020/1/2 21:54<br/>
 *
 * @author 陈荣祥 <br/>
 * @since JDK 1.8
 */
public class BasicTypeStream {
    public static void main(String[] args) {
        intStream();
    }

    public static void intStream() {

        // 基础类型流的创建
        // 范围创建（左闭右开），1-10不包含10
        IntStream.range(1, 10);
        // 范围创建（双闭区间），1-10包含10
        IntStream.rangeClosed(1, 10);
        // 从数组创建，包含1, 2, 3的流
        IntStream.of(1, 2, 3);
        // 使用生成器创建，从1开始，每次递增2的无限int流
        IntStream.iterate(1, i -> i + 2);
        // 从其他类型转换来，使用mapToInt
        int charCount = Stream.of("a", "ab", "abc", "abcd")
                .mapToInt(String::length)
                .sum();
        assert 10 == charCount;

        //流之间的转换
        Stream<Long> longStream = Stream.of("1", "2", "3", "4")
                // 包装类型流转换成IntStream
                .mapToInt(Integer::valueOf)
                // 转换成LongStream
                .asLongStream()
                // 转换成包装类型流
                .boxed();

        // 求和
        assert 10 == IntStream.of(1, 2, 3, 4).sum();
        // 最大值
        assert 4 == IntStream.of(1, 2, 3, 4).max().orElse(0);
        // 最小值
        assert 1 == IntStream.of(1, 2, 3, 4).min().orElse(0);
        // 平均值
        assert 2.5 == IntStream.of(1, 2, 3, 4).average().orElse(0);
        // 个数
        assert 4L == IntStream.of(1, 2, 3, 4).count();

        // 所有常规统计值一次性计算
        IntSummaryStatistics intSummaryStatistics = IntStream.of(1, 2, 3, 4)
                .summaryStatistics();
        assert 1 == intSummaryStatistics.getMin();
        assert 4 == intSummaryStatistics.getMax();
        assert 2.5 == intSummaryStatistics.getAverage();
        assert 4L == intSummaryStatistics.getCount();
    }
}
