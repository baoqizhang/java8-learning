package com.yunzhitx.java8.learning.stream;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

/**
 * ClassName: Create <br/>
 * Description: 流的创建<br/>
 * date: 2019/12/23 15:18<br/>
 *
 * @author 陈荣祥 <br/>
 * @since JDK 1.8
 */
public class Create {

    public static void main(String[] args) {
        // 1.Stream的静态方法 Stream.of(), 转换数组为Stream
        Stream<Integer> stream1 = Stream.of(1, 2, 3);

        // 2.使用Arrays.stream()方法，转换数组为Stream
        Stream<Integer> stream2 = Arrays.stream(new Integer[]{1, 2, 3});

        // 3.Collection.toStream()，转换Collection为Stream
        List<Integer> list = Arrays.asList(1, 2, 3);
        Stream<Integer> stream3 = list.stream();

        // 4.生成一个空的Stream
        Stream<String> emptyStream = Stream.empty();

        // 5.通过Stream.builder()创建包含指定元素的流
        Stream<Integer> buildStream = Stream.<Integer>builder()
                .add(1).add(2).add(3)
                .build();

        // 6.创建一个无限长度的流，这里生成一个无线长度的随机数流
        final Random random = new Random();
        Stream<Integer> randomStream = Stream.generate(() -> random.nextInt(10));
        // 通过limit截取指定长度后打印
        randomStream.limit(10).forEach(System.out::print);

        // 7.使用迭代器创建一个无限的流
        Stream<Integer> iterStream = Stream.iterate(1, prev -> prev * 2);
        // 16
        iterStream.limit(5).forEach(System.out::println);
    }

}
