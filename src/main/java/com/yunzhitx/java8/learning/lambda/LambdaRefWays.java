package com.yunzhitx.java8.learning.lambda;

import java.util.function.BinaryOperator;
import java.util.stream.Stream;

/**
 * ClassName: LambdaRefWays <br/>
 * Description: lambda表达式的引用方式<br/>
 * date: 2019/12/23 9:47<br/>
 *
 * @author 陈荣祥 <br/>
 * @since JDK 1.8
 */
public class LambdaRefWays {

    public static void main(String[] args) {
        statement();
        refMethod();
    }

    /**
     * 调用时直接声明
     */
    public static void statement() {
        // 单行写法，省略语句最后分号
        new Thread(() -> System.out.println("单行写法")).start();

        // 多行写法
        new Thread(() -> {
            System.out.println("多行写法");
            System.out.println("多行写法");
        }).start();

        // 赋值给一个变量/常量的写法
        Runnable runnable = () -> System.out.println("赋值给一个变量/常量的写法");
        new Thread(runnable).start();
    }

    /**
     * 引用已有方法
     */
    public static void refMethod() {
        Stream.of("111", "-222", "333")
                .map(Integer::new) // 引用构造方法
                .map(Math::abs) // 引用静态方法
                .forEach(System.out::println); // 引用实例方法

        // 等价于下面的写法
        Stream.of("111", "-222", "333")
                .map(str -> new Integer(str))
                .map(intVal -> Math.abs(intVal))
                .forEach(val -> System.out.println(val));

        System.out.println(calculate(LambdaRefWays::add));
    }

    public static int calculate(BinaryOperator<Integer> calculator) {
        int a = 3;
        int b = 4;
        return calculator.apply(3, 4);
    }

    public static int add(int a, int b) {
        return a + b;
    }

}
