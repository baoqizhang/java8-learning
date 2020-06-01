package com.yunzhitx.java8.learning.functionalinterface;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * ClassName: PresetFunctionalInterface <br/>
 * Description: <br/>
 * date: 2019/12/23 14:18<br/>
 *
 * @author 陈荣祥 <br/>
 * @since JDK 1.8
 */
@Slf4j
public class PresetFunctionalInterface {

    public static void main(String[] args) {
        useFunction();
        useConsumer();
        usePredicate();
        useSupplier();
    }

    public static void useFunction() {
        List<String> stringList = Stream.of(1, 2, 3)
                // 这里的map方法参数就是一个Function
                .map(String::valueOf)
                .collect(Collectors.toList());
    }

    public static void useConsumer() {
        Stream.of(1, 2, 3)
                // 这里的forEach方法参数就是一个Consumer
                .forEach(System.out::println);
    }

    public static void usePredicate() {
        Stream.of(1, 2, 3)
                // filter的参数就是一个Predicate，用于过滤Stream中的元素，返回true是保留元素
                .filter(i -> i > 2)
                .forEach(System.out::println);

    }

    public static void useSupplier() {
        String value = null;
        // 只有当value为空的，需要抛出异常的时候，才会在内部调用Supplier.get()初始化这段message
        Objects.requireNonNull(value, () -> "value can't be null");

        // 通常打印debug日志的方式
        if (log.isDebugEnabled()) {
            log.debug("some debug messages");
        }

        // 使用封装后的debug方法
        debug(() -> "some debug messages");

    }

    private static void debug(Supplier<String> messageSupplier) {
        if (log.isDebugEnabled()) {
            log.debug(messageSupplier.get());
        }
    }
}
