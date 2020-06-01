package com.yunzhitx.java8.learning.functionalinterface;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * ClassName: ConverterClient <br/>
 * Description: <br/>
 * date: 2019/12/23 13:41<br/>
 *
 * @author 陈荣祥 <br/>
 * @since JDK 1.8
 */
@Data
@AllArgsConstructor
public class CalculatorData {

    private Integer val1;

    private Integer val2;

    private Integer calculate(Calculator<Integer> calculator) {

        return calculator.calculate(this.val1, this.val2);
    }

    public static void main(String[] args) {
        CalculatorData context = new CalculatorData(3, 4);
        // 内联写法：加法
        assert context.calculate((a, b) -> a + b) == 7;

        // 变量引用的写法：减法
        Calculator<Integer> minus = (a, b) -> a - b;
        assert context.calculate(minus) == -1;

        // 方法引用的写法：较大值
        assert context.calculate(Integer::max) == 4;
    }

}
