package com.yunzhitx.java8.learning.stream;

import com.yunzhitx.java8.learning.domain.User;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * ClassName: StreamCollector <br/>
 * Description: <br/>
 * date: 2019/12/30 10:48<br/>
 *
 * @author 陈荣祥 <br/>
 * @since JDK 1.8
 */
public class StreamCollector {

    public static void main(String[] args) {
        // ArrayList收集器
        listCollector();
        // HashSet收集器
        setCollector();
        // HashMap收集器
        mapCollector();
        // 自定义收集器
        customerCollector();
        // 分块收集器
        partitionCollector();
        // 分组收集器
        groupCollector();
        // 聚合收集器
        aggregateCollector();
    }

    public static void listCollector() {
        System.out.println("---list collector---");
        List<Integer> list = Stream.of(1, 2, 3)
                .collect(Collectors.toList());
        System.out.println(list);
    }

    public static void setCollector() {
        System.out.println("---set collector---");
        Set<Integer> set = Stream.of(1, 2, 3)
                .collect(Collectors.toSet());
        System.out.println(set);
    }

    public static void mapCollector() {
        System.out.println("---map collector---");

        // 将User流收集成key为id，value为user对象本身的Map
        Map<Integer, User> map = Stream.of(
                new User().setId(1).setName("Tom"),
                new User().setId(2).setName("Jeff"),
                new User().setId(3).setName("Jack")
        )
        .collect(
                Collectors.toMap(
                        User::getId, // 获取key的Function
                        Function.identity() // 获取value的Function
                )
        );

        System.out.println(map);
    }

    public static void customerCollector() {
        System.out.println("---customer collector---");

        // 第一种方式，用于Collection接口的子类，可以定义具体的实现类
        LinkedList<Integer> linkedList = Stream.of(1, 2, 3)
                .collect(Collectors.toCollection(LinkedList::new));
        System.out.println(linkedList);

        // 第二种方式，用于任何集合，是最灵活的方式
        HashSet<Integer> set = Stream.of(1, 2, 3)
                .collect(
                        HashSet::new, // 定义一个用来收集结果的容器
                        HashSet::add, // 定义每个元素通过什么方法收集到容器中
                        HashSet::addAll // 定义并发情况下如何合并多个容器的结果
                );
        System.out.println(set);
    }

    public static void partitionCollector() {
        System.out.println("---partition collector---");
        // 将奇数和偶数收集到map的两个value中
        Map<Boolean, List<Integer>> oddEvenMap = Stream.of(1, 2, 3, 4, 5)
                .collect(Collectors.partitioningBy(i -> i % 2 == 0));
        System.out.println("偶数：" + oddEvenMap.get(Boolean.TRUE));
        System.out.println("奇数：" + oddEvenMap.get(Boolean.FALSE));
    }

    public static void groupCollector() {
        System.out.println("---partition collector---");
        // 简单分组：默认分组的map value使用的是Collectors.toList()收集器
        // 按用户的职位分组
        Map<String, List<User>> jobUserMap = Stream.of(
                new User().setJob("DEV").setName("Tom"),
                new User().setJob("QA").setName("Jeff"),
                new User().setJob("DEV").setName("Jack")
        )
        .collect(Collectors.groupingBy(User::getJob));
        System.out.println(jobUserMap);

        // 自定义分组收集器
        Map<String, Set<User>> jobUserMap2 = Stream.of(
                new User().setJob("DEV").setName("Tom"),
                new User().setJob("QA").setName("Jeff"),
                new User().setJob("DEV").setName("Jack")
        )
        .collect(Collectors.groupingBy(User::getJob, Collectors.toSet()));
        System.out.println(jobUserMap2);

        // 可以通过自定义收集器进行更多的聚合计算
        Map<String, Integer> jobScoreMap = Stream.of(
                new User().setJob("DEV").setName("Tom").setScore(100),
                new User().setJob("QA").setName("Jeff").setScore(200),
                new User().setJob("DEV").setName("Jack").setScore(300)
        )
        .collect(Collectors.groupingBy(
                User::getJob, // 按职位分组
                Collectors.summingInt(User::getScore) // 收集每个组用户的积分总和
        ));
        System.out.println(jobScoreMap);
    }

    public static void aggregateCollector() {
        System.out.println("aggregate collector");
        // summing求和
        Integer sumResult = Stream.of(1, 2, 3, 4)
                .collect(Collectors.summingInt(Integer::valueOf));
        assert sumResult == 10;

        // averaging求平均值
        Double avgResult = Stream.of(1, 2, 3, 4)
                .collect(Collectors.averagingInt(Integer::valueOf));
        assert avgResult == 2.5D;

        // maxBy，minBy求最大最小值
        Optional<Integer> maxResult = Stream.of(1, 2, 3, 4)
                .collect(Collectors.maxBy(Comparator.comparingInt(Integer::valueOf)));
        assert maxResult.get() == 4;

        // counting计数
        Long countResult = Stream.of(4, 3, 2, 1)
                .collect(Collectors.counting());
        assert countResult == 4;

        // joining字符串连接
        String joinResult = Stream.of("a", "b", "c", "d")
                .collect(Collectors.joining("|"));
        assert "a|b|c|d".equals(joinResult);

        // reduce求和
        Integer reduceResult = Stream.of(1, 2, 3, 4)
                // 初始结果0， 每次遍历时当前元素和结果如何进行性累加
                .collect(Collectors.reducing(0, Integer::sum));
        assert reduceResult == 10;
    }
}
