package com.yunzhitx.java8.learning.completablefuture;

import com.yunzhitx.java8.learning.util.ThreadUtils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * ClassName: CompletableUsage <br/>
 * Description: <br/>
 * date: 2020/1/8 18:49<br/>
 *
 * @author 陈荣祥 <br/>
 * @since JDK 1.8
 */
public class CompletableUsage {
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(3);

    public static void main(String[] args) throws Exception {
        // CompletableFuture的Hello World
        helloWorld();

        // CompletableFuture的创建
        createCompletableFuture();

        // 阻塞获取的API
        blockingApi();

        // 任务链Api
        taskChainApi();

        // 异常处理
        exceptionHandle();

        // CompletableFuture的组合
        composeApi();

        EXECUTOR_SERVICE.shutdown();
    }

    private static void helloWorld() throws ExecutionException, InterruptedException {
        // 在一个新线程中执行task
        CompletableFuture.supplyAsync(() -> 100)
                // 获取到结果后将参数传给下一个函数，对结果进行转换
                .thenApply(Math::sqrt)
                // 然后将转换的结果给下一个Consumer消费
                .thenAccept(System.out::println);

        // 由于ForkJoinPool中的线程默认都是守护线程，
        // 因此，为避免主线程结束程序立即停止，这里需要Sleep一下
        ThreadUtils.sleep(3000);
    }

    private static void createCompletableFuture() {
        System.out.println("createCompletableFuture");

        // 在一个新线程中执行一个有返回值的CompletableFuture
        final CompletableFuture<Integer> completableFuture1 = CompletableFuture.supplyAsync(() -> 100);

        // 在一个新线程中执行一个无返回值的CompletableFuture
        final CompletableFuture<Void> completableFuture2 = CompletableFuture.runAsync(() -> System.out.println("task complete"));

        // 创建一个已经完成的，有返回值的CompletableFuture
        final CompletableFuture<Integer> completableFuture3 = CompletableFuture.completedFuture(100);
    }

    private static void blockingApi() throws ExecutionException, InterruptedException, TimeoutException {
        System.out.println("blockingApi");

        final CompletableFuture<Integer> completableFuture = CompletableFuture.supplyAsync(() -> 100);

        // 阻塞获取结果
        final Integer result = completableFuture.get();

        // 阻塞获取结果，设置超时时间
        final Integer result2 = completableFuture.get(2, TimeUnit.SECONDS);

        // 立即获取结果，如果获取不到，返回参数中指定的默认值
        final Integer result3 = completableFuture.getNow(0);
    }

    private static void taskChainApi() {
        System.out.println("taskChainApi");

        // 在一个新线程中执行任务，获取结果
        CompletableFuture.supplyAsync(() -> 3)
                // thenApply负责将上一个结果转化成另一个值
                .thenApply(result -> result * 2)
                // thenAccept负责消费上一步的结果
                .thenAccept(result -> System.out.println(result))
                // thenRun负责在上一步完成之后执行某些操作，它无法获取到上一步的结果
                .thenRun(() -> System.out.println("task complete"));

        // 异步版本，异步版本和同步版本的唯一区别就是异步的方法在新的线程中执行，而不是在上一步的线程中执行
        CompletableFuture.supplyAsync(() -> 3)
                // 默认使用ForkJoinPool.commonPool
                .thenApplyAsync(result -> result * 2)
                // 这里使用自定义的线程池
                .thenAcceptAsync(result -> System.out.println(result), EXECUTOR_SERVICE)
                .thenRunAsync(() -> {
                    System.out.println("task complete");
                });

        ThreadUtils.sleep(1000);
    }

    private static void exceptionHandle() {
        System.out.println("exceptionHandle");
        final int defaultVal = 0;

        // 使用handle，无论是否有异常都会执行，需要内部判断，如果有异常需要提供一个默认值返回
        // 可以当成带异常处理的thenApply
        CompletableFuture.supplyAsync(CompletableUsage::exceptionTask)
                .handle((result, ex) -> {
                    if (ex != null) {
                        System.out.println(ex.getMessage());
                        return defaultVal;
                    }
                    return result;
                })
                .thenAccept(result -> System.out.println("result is " + result));

        // 使用whenComplete，无论是否有异常都会执行，需要内部判断，如果有异常需要处理异常
        // 可以当成带异常处理的thenAccept，只消费，不产出
        CompletableFuture.supplyAsync(CompletableUsage::exceptionTask)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        System.out.println(ex.getMessage());
                    } else {
                        System.out.println("resutl is " + result);
                    }
                });

        // 使用exceptionally，只有当有异常时才执行该方法，该方法需要处理异常，并返回发生异常时的默认返回值
        // 可以当成带异常处理的thenApply，仅当有异常时执行
        CompletableFuture.supplyAsync(CompletableUsage::exceptionTask)
                .exceptionally(ex -> {
                    System.out.println(ex.getMessage());
                    return defaultVal;
                })
                .thenAccept(result -> System.out.println("result is " + result));
    }

    private static void composeApi() {
        System.out.println("composeApi");

        System.out.println("thenCompose");
        // 将两个CompletableFuture结合，第二个CompletableFuture依赖于第一个的执行结果
        final CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> 100);
        future.thenCompose(result -> createFromPreviousResult(result))
            .thenAccept(result -> System.out.println("result is " + result));
        ThreadUtils.sleep(1000);

        System.out.println("thenCombine");
        // 使用combine，当两个CompletableFuture都完成之后再进行下一步调用
        final CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(() -> 100);
        final CompletableFuture<Integer> future2 = CompletableFuture.supplyAsync(() -> 200);
        future1.thenCombine(future2, (result1, result2) -> result1 + result2)
                .thenAccept(result -> System.out.println("result is " + result));

        System.out.println("allOf");
        // 使用allOf，多个任务都完成时，再进行下一步
        final List<CompletableFuture<Integer>> allFutures = Arrays.asList(
                CompletableFuture.supplyAsync(() -> 100),
                CompletableFuture.supplyAsync(() -> 200),
                CompletableFuture.supplyAsync(() -> 300)
        );
        // allOf返回的CompletableFuture，返回类是Void，没有值，需要从原来的列表中去获取
        CompletableFuture.allOf(allFutures.toArray(new CompletableFuture[3]))
                .thenAccept(voidVal -> {
                    allFutures.stream()
                            .map(CompletableFuture::join)
                            .forEach(result -> System.out.println("result is " + result));
                });

        System.out.println("anyOf");
        // 使用anyOf，多个任务有一个完成时，就执行下一步，哪个先完成就得到哪个任务的结果
        final List<CompletableFuture<Integer>> anyFutures = Arrays.asList(
                CompletableFuture.supplyAsync(() -> 100),
                CompletableFuture.supplyAsync(() -> 200),
                CompletableFuture.supplyAsync(() -> 300)
        );
        CompletableFuture.anyOf(anyFutures.toArray(new CompletableFuture[3]))
                .thenAccept(result -> System.out.println("result is " + result));
    }

    private static CompletableFuture<Integer> createFromPreviousResult(final Integer previousResult) {
        return CompletableFuture.supplyAsync(() -> previousResult + 200);
    }

    private static Integer exceptionTask() {
        if (true) {
            throw new RuntimeException("an exception");
        }
        return 100;
    }

}