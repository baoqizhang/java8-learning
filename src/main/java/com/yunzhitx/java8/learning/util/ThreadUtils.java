package com.yunzhitx.java8.learning.util;

/**
 * ClassName: ThreadUtils <br/>
 * Description: <br/>
 * date: 2020/1/15 9:48<br/>
 *
 * @author 陈荣祥 <br/>
 * @since JDK 1.8
 */
public class ThreadUtils {

    public static void sleep(long milliSeconds) {
        try {
            Thread.sleep(milliSeconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
