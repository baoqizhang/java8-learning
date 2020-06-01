package com.yunzhitx.java8.learning.stream;

import com.yunzhitx.java8.learning.domain.User;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * ClassName: Process <br/>
 * Description: Stream API处理集合中的数据<br/>
 * date: 2019/12/23 15:31<br/>
 *
 * @author 陈荣祥 <br/>
 * @since JDK 1.8
 */
public class Process {

    public static void main(String[] args) {
        // 转换
        map();
        // 过滤
        filter();
        // 展开子集合
        flatMap();
        // 循环消费
        forEach();
        // 是否存在
        match();
        // 去重
        distinct();
        // 计数
        count();
        // 跳过和取n条（分页）
        skipAndLimit();
        // 排序
        sorted();
        // 聚合
        reduce();
        // 预览
        peek();
    }

    public static void map() {
        System.out.println("map");
        List<Integer> list = Arrays.asList(1, 2, 3);
        list.parallelStream();
        List<String> newList = list.stream()
                .map(i -> i * i)
                .map(String::valueOf)
                .collect(Collectors.toList());
        // ["1", "4", "9"]
        System.out.println(newList);
    }

    public static void filter() {
        System.out.println("filter");
        List<Integer> list = Arrays.asList(1, 2, 3);
        List<Integer> newList = list.stream()
                .filter(i -> i > 1)
                .collect(Collectors.toList());
        // [2, 3]
        System.out.println(newList);
    }

    public static void flatMap() {
        System.out.println("flatMap");
        // 假设现在有一组用户列表，每个用户有多个爱好
        List<User> users = Arrays.asList(
                new User()
                        .setId(1)
                        .setName("张三")
                        .setHobbies(Arrays.asList("看书", "听音乐")),
                new User()
                        .setId(2)
                        .setName("李四")
                        .setHobbies(Arrays.asList("看电影", "乒乓球")),
                new User()
                        .setId(3)
                        .setName("王五")
                        .setHobbies(Arrays.asList("爬山", "履行"))
        );

        // 要获取所有用户的所有的爱好，只需要这样
        List<String> hobbies = users.stream()
                .flatMap(user -> user.getHobbies().stream())
                .collect(Collectors.toList());

        // [看书, 听音乐, 看电影, 乒乓球, 爬山, 履行]
        System.out.println(hobbies);
    }

    public static void forEach() {
        System.out.println("forEach");
        // Stream可以调用forEach方法
        Stream.of(1, 2, 3, 4)
                .forEach(System.out::println);

        // List也可以直接调用forEach方法，不用先转成Stream
        Arrays.asList(1, 2, 3, 4)
                .forEach(System.out::println);
    }

    public static void match() {
        System.out.println("match");
        List<Integer> list = Arrays.asList(1, 2, 3);
        boolean anyGreaterThanOne = list.stream().anyMatch(i -> i > 1);
        assert anyGreaterThanOne == true;

        boolean allGreaterThanOne = list.stream().allMatch(i -> i > 1);
        assert allGreaterThanOne == false;

        boolean noneGreaterThanOne = list.stream().noneMatch(i -> i > 1);
        assert noneGreaterThanOne == false;
    }

    public static void distinct() {
        System.out.println("distinct");
        List<Integer> list = Arrays.asList(1, 1, 2, 3, 3);
        List<Integer> distinctList = list.stream()
                .distinct()
                .collect(Collectors.toList());
        // [1, 2, 3]
        System.out.println(distinctList);
    }

    public static void count() {
        System.out.println("count");
        List<Integer> list = Arrays.asList(1, 2, 3);
        long count = list.stream()
                .filter(i -> i > 2)
                .count();
        // 1
        System.out.println(count);
    }

    public static void skipAndLimit() {
        System.out.println("skipAndLimit");
        Stream.iterate(1, prev -> prev + 1) // 1开始，自增1的无限序列
                .skip(6) //  跳过前6个
                .limit(3) // 取3个元素
                .forEach(System.out::print); // 打印出789
        System.out.println("");
    }

    public static void sorted() {
        System.out.println("sorted");
        List<String> list = Stream.of("abcd", "a", "abc", "ab")
                .sorted(Comparator.comparingInt(String::length).reversed()) // 按字符串长度排序，逆序
                .collect(Collectors.toList());
        // [abcd, abc, ab, a]
        System.out.println(list);
    }

    public static void reduce() {
        System.out.println("reduce");
        List<Integer> list = Arrays.asList(1, 2, 3, 4);
        // 求和
        // 初始值为0，累加所有元素
        Integer sum1 = list.stream().reduce(0, (x, y) -> x + y);
        // 10
        System.out.println(sum1);

        // 无初始值，计算结果为Optional，需要处理为空的情况后才能使用
        Integer sum2 = list.stream()
                .reduce(Integer::sum)
                .orElse(0);
        // 10
        System.out.println(sum2);

        // 聚合时目标类型和Stream中元素类型不同时，使用此重载方法
        BigDecimal sum3 = list.stream()
                .reduce(
                        BigDecimal.ZERO, //初始值
                        (prev, i) -> prev.add(BigDecimal.valueOf(i)), // 转换成目标类型后累加
                        BigDecimal::add // 多线程的parallelStream中，对每个线程的累加结果进行汇总的方法
                );
        // 10
        System.out.println(sum3);
    }

    public static void peek() {
        System.out.println("peek");
        String result = Stream.of(1, 2, 3, 4)
                .map(i -> i * i)
                // 在流处理过程中提前窥视一下当前数据的样子
                // 这里会分别打印出 1, 4, 9, 16
                .peek(System.out::println)
                .map(i -> i * 2)
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        assert "2,8,18,32".equals(result);
    }
}
