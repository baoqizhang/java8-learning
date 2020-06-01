package com.yunzhitx.java8.learning.functionalinterface;

/**
 * ClassName: 计算接口 <br/>
 * Description: <br/>
 * date: 2019/12/23 12:04<br/>
 *
 * @author 陈荣祥 <br/>
 * @since JDK 1.8
 */
@FunctionalInterface
public interface Calculator<T> {

    /**
     * 计算
     *
     * @param val1 运算数1
     * @param val2 运算数2
     * @return 计算结果
     */
    T calculate(T val1, T val2);

}
