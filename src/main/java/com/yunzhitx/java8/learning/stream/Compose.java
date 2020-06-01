package com.yunzhitx.java8.learning.stream;

import com.yunzhitx.java8.learning.domain.User;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * ClassName: Compose多个Function组合 <br/>
 * Description: <br/>
 * date: 2019/12/30 9:32<br/>
 *
 * @author 陈荣祥 <br/>
 * @since JDK 1.8
 */
public class Compose {

    public static void main(String[] args) {
        functionCompose();
        consumerCompose();
        predicateCompose();
    }

    public static void functionCompose() {
        System.out.println("--function compose--");
        Function<Integer, Integer> doubleVal = i -> i * 2;
        Function<Integer, String> func = doubleVal
                .andThen(String::valueOf)
                .andThen(str -> "value is " + str);

        // 在stream转换中
        Stream.of(1, 2, 3, 4)
                .map(func)
                .forEach(System.out::println);

        // 直接使用
        String result = func.apply(5);
        assert "value is 10".equals(result);
    }

    public static void consumerCompose() {
        System.out.println("--consumer compose--");
        final StringBuilder sb = new StringBuilder();

        // 控制台消费后StringBuilder再消费一次
        Consumer<String> consumer = ((Consumer<String>) System.out::println)
                .andThen(sb::append);

        Stream.of(1, 2, 3, 4)
                .map(String::valueOf)
                .forEach(consumer);
        assert "1234".equals(sb.toString());
    }

    public static void predicateCompose() {
        User user = new User().setName("Clark");
        // user != null 和 user.getName() != null 用and组合
        Predicate<User> userPredicate = ((Predicate<User>) u -> u != null)
                .and(u -> u.getName() != null);

        User theUser = Optional.ofNullable(user)
                .filter(userPredicate)
                .orElseThrow(() -> new IllegalArgumentException("用户和用户名不能为空"));
        assert "Clark".equals(theUser.getName());
    }
}
