package com.yunzhitx.java8.learning.lambda;

import java.util.concurrent.*;

/**
 * ClassName: QuickStart <br/>
 * Description: <br/>
 * date: 2019/12/20 18:03<br/>
 *
 * @author 陈荣祥 <br/>
 * @since JDK 1.8
 */
public class QuickStart {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        legacy();
        lambda();
    }

    public static void legacy() {
        // 无返回值
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("worker thread is running");
            }
        }).start();

        // 有返回值
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return "result";
            }
        });
    }

    public static void lambda() throws ExecutionException, InterruptedException {
        // 无返回值
        new Thread(() -> System.out.println("worker thread is running")).start();

        // 有返回值时，单行写法需要省略return，多行写法不能省略return
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<String> future1 = executorService.submit(() -> "result");
        Future<String> future2 = executorService.submit(() -> {
            return "result";
        });
        System.out.println(future1.get());
        System.out.println(future2.get());
    }
}
