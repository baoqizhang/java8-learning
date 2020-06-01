package com.yunzhitx.java8.learning.map;

import java.util.HashMap;
import java.util.Map;

/**
 * ClassName: MapEnhancement <br/>
 * Description: <br/>
 * date: 2020/1/3 0:16<br/>
 *
 * @author 陈荣祥 <br/>
 * @since JDK 1.8
 */
public class MapEnhancement {
    private static final Map<String, Integer> MAP = new HashMap<>();

    static {
        MAP.put("a", 1);
        MAP.put("b", 2);
        MAP.put("c", 3);
        MAP.put("d", 4);
    }

    public static void main(String[] args) {
        // 遍历
        forEach();
        // 获取默认值
        getOrDefault();
        // key不存在时才put
        putIfAbsent();
        // key不存在就根据key计算一个新的值，put并返回值
        computeIfAbsent();
        // key存在就根据key和老的值计算一个新值，put并返回值
        computeIfPresent();
        // 合并key的老值和给定值为一个新值，put并返回
        merge();
    }

    public static void forEach() {
        System.out.println("forEach");
        // 遍历时，map的key和value将作为lambda表达式的参数
        MAP.forEach((key, val) -> System.out.println("key = " + key + ", value = " + val));
    }

    private static void getOrDefault() {
        System.out.println("getOrDefault");
        // 当key不存在时，第二个参数作为默认值返回
        assert 100 == MAP.getOrDefault("test", 100);

        // 等价于下面的操作
        assert 100 == (MAP.get("test") != null ? MAP.get("test") : 100);
    }

    private static Integer putIfAbsent() {
        assert null == MAP.get("e");
        // 不存在e这个key就把5放进去
        assert null == MAP.putIfAbsent("e", 5);
        assert 5 == MAP.get("e");

        // 等价于下面的操作
        final Integer oldValue = MAP.get("e");
        if (oldValue == null) {
            MAP.put("e", 5);
        }
        return oldValue;
    }

    private static Integer computeIfAbsent() {
        // key对应的值不存在，就根据key计算一个值put到Map中，并返回这个值
        assert 5 == MAP.computeIfAbsent("hello", key -> key.length());
        assert 5 == MAP.get("hello");

        // 等价于
        if (MAP.get("hello") == null) {
            final int newValue = "hello".length();
            MAP.put("hello", newValue);
        }
        return MAP.get("hello");
    }

    private static Integer computeIfPresent() {
        assert 1 == MAP.get("a");
        // 如果key存在就计算一个新的值替换老值
        assert 2 == MAP.computeIfPresent("a", (oldKey, oldVal) -> oldVal * 2);
        assert 2 == MAP.get("a");

        // 等价于
        if (MAP.get("a") != null) {
            MAP.put("a", MAP.get("a") * 2);
             return MAP.get("a");
        } else {
            return null;
        }
    }

    private static void merge() {
        assert 2 == MAP.get("b");
        // 合并key的老值和指定值为一个新值设置进去，put并返回，如果老值不存在就直接把给定值作为新值设置进去，不用进行合并
        assert 5 == MAP.merge("b", 3, (oldVal, givenVal) -> oldVal + givenVal);
        assert 5 == MAP.get("b");
    }
}
