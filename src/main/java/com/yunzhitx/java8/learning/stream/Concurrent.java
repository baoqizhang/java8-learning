package com.yunzhitx.java8.learning.stream;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * ClassName: 并行流 <br/>
 * Description: <br/>
 * date: 2019/12/31 10:33<br/>
 *
 * @author 陈荣祥 <br/>
 * @since JDK 1.8
 */
public class Concurrent {
    private static final ExecutorService CUSTOM_THREAD_POOL = new ThreadPoolExecutor(
            20, 30, 1, TimeUnit.MINUTES,
            new LinkedBlockingDeque<>(100), new ThreadPoolExecutor.DiscardPolicy()
    );

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        usage();
        useCustomerThreadPool();
    }

    public static void usage() {
        System.out.println("---usage---");
        List<String> list = Stream.of(1, 2, 3, 4)
                .parallel()
                .map(String::valueOf)
                .collect(Collectors.toList());
        System.out.println(list);
    }

    public static void useCustomerThreadPool() throws ExecutionException, InterruptedException {
        System.out.println("---use customer thread pool---");
        final Stream<Integer> stream = Stream.of(1, 2, 3, 4);
        Future<String> result = CUSTOM_THREAD_POOL.submit(() -> {
            return stream.map(String::valueOf).collect(Collectors.joining());
        });
        System.out.println(result.get());
    }
}
