package com.yunzhitx.java8.learning.optional;

import com.yunzhitx.java8.learning.domain.Address;
import com.yunzhitx.java8.learning.domain.User;

import java.util.Optional;

/**
 * ClassName: OptionalUsage <br/>
 * Description: <br/>
 * date: 2019/12/31 10:32<br/>
 *
 * @author 陈荣祥 <br/>
 * @since JDK 1.8
 */
public class OptionalUsage {

    public static void main(String[] args) {
        // 创建Optional
        create();
        // 判断是否为空
        judgement();
        // 获取被包装的对象
        get();
        // 转换
        map();
    }

    public static void create() {
        // of的参数如果为空会抛出空指针异常
        Optional<Integer> num1 = Optional.of(123);

        // 如果需要包装的对象可能为空，则需要使用ofNullable
        Optional<Object> num2 = Optional.ofNullable(null);

        // 创建一个空对象
        Optional<Object> empty = Optional.empty();
    }

    public static void judgement() {
        // isPresent判断被包装的对象是否存在
        assert false == Optional.empty().isPresent();

        // ifPresent函数接受一个Consumer作为参数，用于处理被包装对象不为空时的值
        // 当Optional的包装对象为空时，将什么都不会做
        Optional.ofNullable("我不为空")
                .ifPresent(System.out::println);
    }

    public static void get() {
        // get方法，当被包装对象不为空时才正常返回，为空时将抛出NoSuchElementException异常，一般不用此方法
        assert 123 == Optional.ofNullable(123).get();

        // orElse当作为被包装对象为空时的默认值返回
        Integer nullVal = null;
        assert 123 == Optional.ofNullable(nullVal)
                .orElse(123);

        // orElseGet和orElse作用相同，只不过接受的参数是一个Supplier，用于延迟初始化默认值
        assert 123 == Optional.ofNullable(nullVal)
                .orElseGet(() -> 123);

        // orElseThrow在被包装对象为空时抛出指定异常
        try {
            Optional.ofNullable(nullVal)
                    .orElseThrow(() -> new IllegalArgumentException("为空"));
        } catch (IllegalArgumentException e) {
            System.out.println("成功抛出异常");
            assert "为空".equals(e.getMessage());
        }
    }

    public static void map() {
        final User user = new User()
                .setId(1).setName("Clark").setJob("Java")
                .setAddress(new Address().setCountry("China"));

        // 假设现在要判断当用户的工作是"Java"的时候，获取用户的居住国家
        String country = null;
        // 在没有Optional时，为了处理空指针异常，我们通常这么写
        if (user != null
                && "Java".equals(user.getJob())
                && user.getAddress() != null
                && user.getAddress().getCountry() != null) {

            country = user.getAddress().getCountry();
        }
        assert "China".equals(country);

        // 使用Optional后，就会变成这样
        String country2 = Optional.ofNullable(user)
                .filter(u -> "Java".equals(u.getJob()))
                .map(User::getAddress)
                .map(Address::getCountry)
                .orElse(null);

        assert "China".equals(country2);
    }
}
