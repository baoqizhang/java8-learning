package com.yunzhitx.java8.learning.stream;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * ClassName: Other <br/>
 * Description: <br/>
 * date: 2020/1/2 22:37<br/>
 *
 * @author 陈荣祥 <br/>
 * @since JDK 1.8
 */
public class Other {

    public static void main(String[] args) {
        longLambda();
        nullCheck();
        try {
            exceptionThrow();
        } catch (IOException e) {
            System.out.println("Outer space has caught Exception from Lambda");
        }
    }

    public static void longLambda() {
        // 不好的风格
        Stream.of(1, 2, 3, 4)
                .map(i -> {
                    int doubleVal = i * 2;
                    String str = "The double value is: " + doubleVal;
                    byte[] encodedData = Base64.getEncoder().encode(str.getBytes());
                    return new String(encodedData, UTF_8);
                })
                .forEach(System.out::println);

        // 推荐的风格
        Stream.of(1, 2, 3, 4)
                .map(Other::convertAndEncode)
                .forEach(System.out::println);
    }

    public static void nullCheck() {
        List<Integer> list = null;
        // 下面的代码做了空检查，因此不会抛空指针异常
        // 前两行是模板，可以封装到工具类中
        Optional.ofNullable(list)
                .orElseGet(Collections::emptyList)
                .stream()
                .map(String::valueOf)
                .forEach(System.out::println);

    }

    public static String convertAndEncode(final Integer intVal) {
        int doubleVal = intVal * 2;
        String str = "The double value is: " + doubleVal;
        byte[] encodedData = Base64.getEncoder().encode(str.getBytes());
        return new String(encodedData, UTF_8);
    }

    public static void exceptionThrow() throws IOException {
        // 下面注释的代码会有编译错误
        /*Arrays.asList(1, 2, 3)
                .forEach(i -> {
                    throw new IOException("message");
                });*/
        Arrays.asList(1, 2, 3)
            .forEach(rethrowConsumer(t -> {
                    throw new IOException("message");
                })
            );
    }

    @FunctionalInterface
    private interface ConsumerWithException<T, E extends Exception> {
        void accept(T t) throws E;
    }

    private static  <T, E extends Exception> Consumer<T> rethrowConsumer(ConsumerWithException<T, E> consumer) throws E {
        return t -> {
            try {
                consumer.accept(t);
            } catch (Exception exception) {
                throwAsUnchecked(exception);
            }
        };
    }

    @SuppressWarnings ("unchecked")
    private static <E extends Exception> void throwAsUnchecked(Exception exception) throws E {
        throw (E)exception;
    }
}
