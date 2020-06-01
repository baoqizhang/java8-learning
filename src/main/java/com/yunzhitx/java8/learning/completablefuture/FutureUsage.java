package com.yunzhitx.java8.learning.completablefuture;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * ClassName: FutureUsage <br/>
 * Description: <br/>
 * date: 2020/1/8 18:57<br/>
 *
 * @author 陈荣祥 <br/>
 * @since JDK 1.8
 */
public class FutureUsage {

    public static void main(String[] args) throws Exception {
        final FutureTask<String> futureTask = new FutureTask<>(new MyTask());
        new Thread(futureTask).start();
        // 在结果返回之前一直阻塞
        final String result = futureTask.get();
        System.out.println(result);
    }

    private static class MyTask implements Callable<String> {
        @Override
        public String call() throws Exception {
            System.out.println("Task is running");
            Thread.sleep(2000);
            System.out.println("Task completed");
            return "The result";
        }
    }
}
