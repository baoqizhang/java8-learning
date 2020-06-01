# Java8特性

[TOC]

## 1. Lambda 表达式

### 与传统匿名内部类写法的对比
无返回值
```java
// 传统写法
new Thread(new Runnable() {
    @Override
    public void run() {
        System.out.println("worker thread is running");
    }
}).start();

// lambda表达式的写法
new Thread(() -> System.out.println("worker thread is running")).start();
```
有返回值
```java
// 传统写法
ExecutorService executorService = Executors.newSingleThreadExecutor();
executorService.submit(new Callable<String>() {
    @Override
    public String call() throws Exception {
        return "result";
    }
});

// lambda表达式的写法
ExecutorService executorService = Executors.newSingleThreadExecutor();
// 行内写法需要省略return
Future<String> future1 = executorService.submit(() -> "result");
// 多行写法return不能省略
Future<String> future2 = executorService.submit(() -> {
    return "result";
});
```

### lambda表达式的使用方式
- 使用时声明
```java
// 单行写法，省略语句最后分号
new Thread(() -> System.out.println("单行写法")).start();

// 多行写法
new Thread(() -> {
    System.out.println("多行写法");
    System.out.println("多行写法");
}).start();

// 赋值给一个变量/常量的写法
Runnable runnable = () -> System.out.println("赋值给一个变量/常量的写法");
new Thread(runnable).start();
```

- 引用已有方法
```java
Stream.of("111", "-222", "333")
        .map(Integer::new) // 引用构造方法
        .map(Math::abs) // 引用静态方法
        .forEach(System.out::println); // 引用实例方法

// 等价于下面的写法
Stream.of("111", "-222", "333")
        .map(str -> new Integer(str))
        .map(intVal -> Math.abs(intVal))
        .forEach(val -> System.out.println(val));
```
> 当lambda表达式的方法体仅仅是一个方法，并且lambda所有参数和这个方法的所有参数一样的时候可以使用"::"将方法转的引用化为lambda表达式

**Lambda表达式实质上是一个只有一个方法的接口的实例，而这个接口有个名字叫函数式接口（Functional Interface）**

## 2. 函数式接口（Functional Interface)

函数式接口是只有一个方法的接口，可以用注解@FunctionalInterface标记在接口上，例如

```java
@FunctionalInterface
public interface Calculator<T> {

    /**
     * 计算
     * @param val1 运算数1
     * @param val2 运算数2
     * @return 计算结果
     */
    T calculate(T val1, T val2);

}
```

> 注：@FunctionalInterface注解并不是必须的，但是可以帮助编译器检查你的接口是否只拥有一个方法，如果有多个方法，编译器会报错。

凡是只有一个函数的接口，都可以使用lambda表达式来定义其实现，比如针对上面的Calculator接口，可以按照下面的方式使用：

```java
// 声明换一个方法的参数包含Calculator
@Data
@AllArgsConstructor
public class CalculatorData {

    private Integer val1;

    private Integer val2;

    private Integer calculate(Calculator<Integer> calculator) {

        return calculator.calculate(this.val1, this.val2);
    }
}

// 调用端写法
CalculatorData context = new CalculatorData(3, 4);
// 内联写法：加法
assert context.calculate((a, b) -> a + b) == 7;

// 变量引用的写法：减法
Calculator<Integer> minus = (a, b) -> a - b;
assert context.calculate(minus) == -1;

// 方法引用的写法：较大值
assert context.calculate(Integer::max) == 4;
```

从上面代码可以看出，使用函数式接口可以简化策略模式的写法，也让策略的定义更加灵活。

### Java内置的函数式接口

jdk1.8已经为我们提供了很多常用的函数式接口供我们使用，所有的接口定义，可以查看jdk源码的`java.util.function`包，这里介绍几种常见的函数式接口

#### 1. Function

一个入参，有返回值，常用于类型转换的场景。

```java
List<String> stringList = Stream.of(1, 2, 3)
                // 这里的map方法参数就是一个Function
                .map(String::valueOf)
                .collect(Collectors.toList());
```

Stream的map方法的参数就是一个Function，用于将stream中的元素转换成另一个元素

##### Function的组合

组合可以将多个Function的计算联合成一个Function，在下面的例子中，组合了三个Function操作：

1. 将一个Integer x 2

2. 将x2后的数字转换成字符串

3. 在转换后的字符串前面拼上“value is ”

最后组合成一个Integer输入，String输出的Function。

```java
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
```



#### 2. Consumer

一个参数，无返回值，常用于回调函数中消费某个值。

```java
Stream.of(1, 2, 3)
        // 这里的forEach方法参数就是一个Consumer
        .forEach(System.out::println);
```

##### Consumer的组合

```java
final StringBuilder sb = new StringBuilder();

// 控制台消费后StringBuilder再消费一次
Consumer<String> consumer = ((Consumer<String>) System.out::println)
    .andThen(sb::append);

Stream.of(1, 2, 3, 4)
    .map(String::valueOf)
    .forEach(consumer);
assert "1234".equals(sb.toString());
```

#### 3. Predicate

一个入参，返回值为boolean类型，常用于某种条件判定。

```java
Stream.of(1, 2, 3)
        // filter的参数就是一个Predicate，用于过滤Stream中的元素，返回true是保留元素
        .filter(i -> i > 2)
        .forEach(System.out::println);
// 这里只会打印出3
```

Predicate的组合

```java
User user = new User().setName("Clark");
// user != null 和 user.getName() != null 用and组合
Predicate<User> userPredicate = ((Predicate<User>) u -> u != null)
    .and(u -> u.getName() != null);

User theUser = Optional.ofNullable(user)
    .filter(userPredicate)
    .orElseThrow(() -> new IllegalArgumentException("用户和用户名不能为空"));
assert "Clark".equals(theUser.getName());
```

#### 4. Supplier

没有参数，只要返回值，常用于延迟初始化某个值。

```java
String value = null;
// 只有当value为空的，需要抛出异常的时候，才会在内部调用Supplier.get()初始化这段message
Objects.requireNonNull(value, () -> "value can't be null");
```

延迟初始化对与创建哪些 不需要立即创建的一些大对象 或 频繁创建，但很少真正使用的对象很有用，例如打印日志，通常情况下我们会先判断当前的日志级别是否是debug级别，如果是，才打印debug日志，可以避免无谓的日志信息String对象的创建：

```java
// 通常打印debug日志的方式
if (log.isDebugEnabled()) {
    log.debug("some debug messages");
}
```

我们通常会在项目中发现很多上述重复的代码，现在有了lambda和Supplier接口，我们可以对日志类加一层代理：

```java
public void debug(Supplier<String> messageSupplier) {
    if (log.isDebugEnabled()) {
        log.debug(messageSupplier.get());
    }
}
```

在调用时，只需要

```java
debug(() -> "some debug messages");
```

上面的lambda表达式并不会真正初始化方法中的字符串，而是延迟到debug方法内部，if为空，真正用到的时候才会初始化这个字符串。这样大大简化了客户端使用的方式。

#### 5. 其他基于上述4中基本函数式接口的扩展接口

- Bi或者Binary开头的代表两个参数的版本，比如BiFunction是两个参数版本的Function，BiConsumer是两个参数版本的Consumer
- UnaryOperator是入参和返回值类型一样的Function，BinaryOperator是两个入参和返回值类型一样的BiFunction，例如上面的Calculator其实就是一个BinaryOperator。
- 其他Int，Long，Double开头的函数式接口都是针对int long double这些非包装类型的封装，使用这些函数式接口可以更节省资源，因为一个int只占4个字节，而一个Integer对象要占16个字节。




## 3. Stream API
Stream API是java8中最常用的处理集合的API，充分运用的lambda表达式的优势，让程序员站在更高的抽象层面对集合进行操作
### 3.1 Stream的创建方式
Stream的创建常用的方式有以下几种
```java
// 1.Stream的静态方法 Stream.of(), 转换数组为Stream
Stream<Integer> stream1 = Stream.of(1, 2, 3);

// 2.使用Arrays.stream()方法，转换数组为Stream
Stream<Integer> stream2 = Arrays.stream(new Integer[]{1, 2, 3});

// 3.Collection.toStream()，转换Collection为Stream
Stream<Integer> stream3 = Arrays.asList(1, 2, 3).stream();

// 4.生成一个空的Stream
Stream<String> emptyStream = Stream.empty();

// 5.通过Stream.builder()创建包含指定元素的流
Stream<Integer> buildStream = Stream.<Integer>builder()
    .add(1).add(2).add(3)
    .build();

// 6.创建一个无限长度的流，这里生成一个无线长度的随机数流
final Random random = new Random();
Stream<Integer> randomStream = Stream.generate(() -> random.nextInt(10));
// 通过limit截取指定长度后打印
randomStream.limit(10).forEach(System.out::print);

// 7.使用迭代器创建一个无限的流
Stream<Integer> iterStream = Stream.iterate(1, prev -> prev * 2);
// 16
iterStream.limit(5).forEach(System.out::println);
```



### 3.2 Stream的处理API
#### Stream.map() 转换
一个类型的列表转换成另一个类型的列表
```java
List<Integer> list = Arrays.asList(1, 2, 3);
List<String> newList = list.stream()
    .map(i -> i * i)
    .map(String::valueOf)
    .collect(Collectors.toList());
// ["1", "4", "9"]
System.out.println(newList);
```
#### Stream.filter() 过滤
filter的表达式，判断结果为true的元素保留下来
```java
List<Integer> list = Arrays.asList(1, 2, 3);
List<Integer> newList = list.stream()
        .filter(i -> i > 1)
        .collect(Collectors.toList());
// [2, 3]
System.out.println(newList);
```

#### Stream.flatMap() 展开子集合
展开集合中每个元素的子集合到一个的集合中

![](https://cdn.cnbj1.fds.api.mi-img.com/book/images/ecc9d917f0bd4f3a4c55e0bd7f060736?thumb=1&w=384&h=384)

```java
// 假设现在有一组用户列表，每个用户有多个爱好
User user1 = new User()
        .setId(1)
        .setName("张三")
        .setHobbies(Arrays.asList("看书", "听音乐"));

User user2 = new User()
        .setId(2)
        .setName("李四")
        .setHobbies(Arrays.asList("看电影", "乒乓球"));

User user3 = new User()
        .setId(3)
        .setName("王五")
        .setHobbies(Arrays.asList("爬山", "履行"));

List<User> users = Arrays.asList(user1, user2, user3);

// 要获取所有用户的所有的爱好，只需要这样
List<String> hobbies = users.stream()
        .flatMap(user -> user.getHobbies().stream())
        .collect(Collectors.toList());

// [看书, 听音乐, 看电影, 乒乓球, 爬山, 履行]
System.out.println(hobbies);
```

#### Stream.forEach() 循环消费
```java
 // Stream可以调用forEach方法
Stream.of(1, 2, 3, 4)
    .forEach(System.out::println);

// List也可以直接调用forEach方法，不用先转成Stream
Arrays.asList(1, 2, 3, 4)
    .forEach(System.out::println);
```

#### Stream.anyMatch()、allMatch()、noneMatch() 检查是否存在元素

```java
List<Integer> list = Arrays.asList(1, 2, 3);
boolean anyGreaterThanOne = list.stream().anyMatch(i -> i > 1);
assert anyGreaterThanOne == true;

boolean allGreaterThanOne = list.stream().allMatch(i -> i > 1);
assert allGreaterThanOne == false;

boolean noneGreaterThanOne = list.stream().noneMatch(i -> i > 1);
assert noneGreaterThanOne == false;
```

#### Stream.distinct() 去重

```java
List<Integer> list = Arrays.asList(1, 1, 2, 3, 3);
List<Integer> distinctList = list.stream()
    .distinct()
    .collect(Collectors.toList());
// [1, 2, 3]
System.out.println(distinctList);
```

#### Stream.count() 计数

```java
List<Integer> list = Arrays.asList(1, 2, 3);
long count = list.stream()
    .filter(i -> i > 2)
    .count();
// 1
System.out.println(count);
```

#### Stream.skip()、limit() 分页

```java
Stream.iterate(1, prev -> prev + 1) // 1开始，自增1的无限序列
    .skip(6) //  跳过前6个
    .limit(3) // 取3个元素
    .forEach(System.out::print); // 打印出789
```

#### Stream.sorted() 排序

```java
List<String> list = Stream.of("abcd", "a", "abc", "ab")
    .sorted(Comparator.comparingInt(String::length).reversed()) // 按字符串长度排序，逆序
    .collect(Collectors.toList());
// [abcd, abc, ab, a]
System.out.println(list);
```

#### Stream.reduce() 聚合

```java
// 求和的例子
List<Integer> list = Arrays.asList(1, 2, 3, 4);

// 初始值为0，累加所有元素
Integer sum1 = list.stream().reduce(0, (x, y) -> x + y);
// 10
System.out.println(sum1);

// 无初始值，计算结果为Optional，需要处理为空的情况后才能使用，因为结果stream中可能没有元素
Integer sum2 = list.stream()
    .reduce(Integer::sum)
    .orElse(0);
// 10
System.out.println(sum2);

// 聚合时目标类型和Stream中元素类型不同时，使用此重载方法
BigDecimal sum3 = list.stream()
    .reduce(
        BigDecimal.ZERO, //初始值
        (prev, i) -> prev.add(BigDecimal.valueOf(i)), // 转换成目标类型后累加
        BigDecimal::add // 多线程的parallelStream中，对每个线程的累加结果进行汇总的方法
    );
// 10
System.out.println(sum3);
```

#### Stream.peek() 预览

```java
String result = Stream.of(1, 2, 3, 4)
    .map(i -> i * i)
    // 在流处理过程中提前窥视一下当前数据的样子
    // 这里会分别打印出 1, 4, 9, 16
    .peek(System.out::println)
    .map(i -> i * 2)
    .map(String::valueOf)
    .collect(Collectors.joining(","));
assert "2,8,18,32".equals(result);
```



### 3.3 Stream的收集器

Stream的收集器是将Stream转换成具体的集合工具，Stream本身并不

Java 8的Stream本身在得到数据前并不会立即执行计算，而是将一系列操作按照顺序组合起来，形成一条方法链，只有在最后进行聚合、消费和收集的时候才会对Stream中的元素执行实际操作。这些惰性执行的方法判断起来也很容易，只需要打开Stream的源码，那些返回值是Stream的方法，就是惰性方法。

#### 简单集合收集器

```java
// 收集到ArrayList中
List<Integer> list = Stream.of(1, 2, 3)
    .collect(Collectors.toList());

// 收集到HashSet中
Set<Integer> set = Stream.of(1, 2, 3)
    .collect(Collectors.toSet());

// 收集到HashMap中
Map<Integer, User> map = Stream.of(
    new User().setId(1).setName("Tom"),
    new User().setId(2).setName("Jeff"),
    new User().setId(3).setName("Jack")
)
.collect(
    Collectors.toMap(
        User::getId, // 获取key的Function
        Function.identity()// 获取value的Function
    )
);

// 自定义收集器
// 第一种方式，用于Collection接口的子类，可以定义具体的实现类
LinkedList<Integer> linkedList = Stream.of(1, 2, 3)
    .collect(Collectors.toCollection(LinkedList::new));

// 第二种方式，用于任何集合，是最灵活的方式
HashSet<Integer> set = Stream.of(1, 2, 3)
    .collect(
        HashSet::new, // 定义一个用来收集结果的容器
        HashSet::add, // 定义每个元素通过什么方法收集到容器中
        HashSet::addAll // 定义并发情况下如何合并多个容器的结果
    );
```

#### 分块和分组收集

##### 分块

分块根据判断条件，分为判断结果为true和判断条件为false的两块集合

![](https://cdn.cnbj1.fds.api.mi-img.com/book/images/3d2097bb2ae0857ea7e79e77bf6bbc52?thumb=1&w=384&h=384)

```java
// 将奇数和偶数收集到map的两个List value中
Map<Boolean, List<Integer>> oddEvenMap = Stream.of(1, 2, 3, 4, 5)
    .collect(Collectors.partitioningBy(i -> i % 2 == 0));
System.out.println("偶数：" + oddEvenMap.get(Boolean.TRUE));
System.out.println("奇数：" + oddEvenMap.get(Boolean.FALSE));
```

##### 分组

分组是根据元素的某个属性，把集合分布到map的多个value集合中

![](https://cdn.cnbj1.fds.api.mi-img.com/book/images/b0ac0758b5700d8b6c2e61d9d84b5413?thumb=1&w=384&h=384)

```java
// 简单分组：默认分组的map value使用的是Collectors.toList()收集器
// 按用户的职位分组
Map<String, List<User>> jobUserMap = Stream.of(
    new User().setJob("DEV").setName("Tom"),
    new User().setJob("QA").setName("Jeff"),
    new User().setJob("DEV").setName("Jack")
)
.collect(Collectors.groupingBy(User::getJob));
System.out.println(jobUserMap);

// 自定义分组收集器
Map<String, Set<User>> jobUserMap2 = Stream.of(
    new User().setJob("DEV").setName("Tom"),
    new User().setJob("QA").setName("Jeff"),
    new User().setJob("DEV").setName("Jack")
)
.collect(Collectors.groupingBy(User::getJob, Collectors.toSet()));
System.out.println(jobUserMap2);

// 可以通过自定义收集器进行更多的聚合计算
Map<String, Integer> jobScoreMap = Stream.of(
    new User().setJob("DEV").setName("Tom").setScore(100),
    new User().setJob("QA").setName("Jeff").setScore(200),
    new User().setJob("DEV").setName("Jack").setScore(300)
)
.collect(Collectors.groupingBy(
	User::getJob, // 按职位分组
    Collectors.summingInt(User::getScore) // 收集每个组用户的积分总和
));
System.out.println(jobScoreMap);
```

> 和goupingBy一样，partitionBy同样也支持自定义收集器

#### 聚合收集器

jdk内置了很多聚合收集器，包括：

- Collectors.summingInt() 求和
- Collectors.averagingInt() 求平均值
- Collectors.maxBy() 求最大值
- Collectors.minBy() 求最小值
- Collectors.counting() 计数
- Collectors.joining() 字符串连接
- Collectors.reducing() 自定义聚合

其中reducing最为灵活，可以实现其他聚合收集器的功能。

```java
// summing 求和
Integer sumResult = Stream.of(1, 2, 3, 4)
    .collect(Collectors.summingInt(Integer::valueOf));
assert sumResult == 10;

// averaging 求平均值
Double avgResult = Stream.of(1, 2, 3, 4)
    .collect(Collectors.averagingInt(Integer::valueOf));
assert avgResult == 2.5D;

// maxBy，minBy 求最大最小值
Optional<Integer> maxResult = Stream.of(1, 2, 3, 4)
    .collect(Collectors.maxBy(Comparator.comparingInt(Integer::valueOf)));
assert maxResult.get() == 4;

// counting 计数
Long countResult = Stream.of(4, 3, 2, 1)
    .collect(Collectors.counting());
assert  countResult == 4;

// joining 字符串连接
String joinResult = Stream.of("a", "b", "c", "d")
    .collect(Collectors.joining("|"));
assert "a|b|c|d".equals(joinResult);

// reduce 自定义方式求和
Integer reduceResult = Stream.of(1, 2, 3, 4)
    // 两个参数分别为初始结果值和累加函数
    // 初始结果0， 每次遍历时当前元素和结果如何进行性累加
    .collect(Collectors.reducing(0, Integer::sum));
assert reduceResult == 10;
```

### 3.4 基本数据流

对于一些基本类型如int, double, long，在使用Stream是不得不包装成Integer，Double，Long，因为Stream本身使用了泛型。为了解决装箱拆箱带来的性能损耗，jdk为我们专门提供了一些处理这类数据的流，并且还提供了一些额外的统计方法。

这些基础数据流分别是`IntStream`，`LongStream`和`DoubleStream`。

这里只以`IntStream`来举例，其他两个和他差不读，看源码的方法声明就可以类推了。

#### 基础数据流的创建

```java
// 范围创建（左闭右开），1-10不包含10
IntStream.range(1, 10);
// 范围创建（双闭区间），1-10包含10
IntStream.rangeClosed(1, 10);
// 从数组创建，包含1, 2, 3的流
IntStream.of(1, 2, 3);
// 使用生成器创建，从1开始，每次递增2的无限int流
IntStream.iterate(1, i -> i + 2);
// 从其他类型转换来，使用mapToInt
int charCount = Stream.of("a", "ab", "abc", "abcd")
    .mapToInt(String::length)
    .sum();
assert 10 == charCount;
```

#### 对象流，基础数据流之间的转换

```java
//流之间的转换
Stream<Long> longStream = Stream.of("1", "2", "3", "4")
    // 包装类型流转换成IntStream
    .mapToInt(Integer::valueOf)
    // IntStream转换成LongStream
    .asLongStream()
    // 转换成包装类型流
    .boxed();
```

#### 一些增强的统计方法

```java
// 求和
assert 10 == IntStream.of(1, 2, 3, 4).sum();
// 最大值
assert 4 == IntStream.of(1, 2, 3, 4).max().orElse(0);
// 最小值
assert 1 == IntStream.of(1, 2, 3, 4).min().orElse(0);
// 平均值
assert 2.5 == IntStream.of(1, 2, 3, 4).average().orElse(0);
// 个数
assert 4L == IntStream.of(1, 2, 3, 4).count();

// 所有常规统计值一次性计算
IntSummaryStatistics intSummaryStatistics = IntStream.of(1, 2, 3, 4)
    .summaryStatistics();
assert 1 == intSummaryStatistics.getMin();
assert 4 == intSummaryStatistics.getMax();
assert 2.5 == intSummaryStatistics.getAverage();
assert 4L == intSummaryStatistics.getCount();
```



### 3.5 Stream并行流

上面的所有StreamAPI都是串行执行的，一个元素处理完了才会处理下一个元素，同时JDK也为我们提供了一种并行流，即Stream的多线程版。这个多线程版的Stream会利用ForkJoinPool.commonPool()的线程池来处理任务。

#### 基本用法

```java
List<String> list = Stream.of(1, 2, 3, 4)
    .parallel()
    .map(String::valueOf)
    .collect(Collectors.toList());
```

#### 使用自定义线程池

需要注意的是，由于ForkJoinPool.commonPool是整个JVM共享的固定大小的线程池，线程池的大小为***CPU核心数-1***。如果遇到同一时间大量请求到服务器，且都是IO密集型的操作，会造成线程池中的线程全部被阻塞在IO过程中，而其他并行流只能等待线程池释放空闲线程。

可以根据业务情况使用线程数更大一点的，有队列和可扩容的线程池：

```java
private static final ExecutorService CUSTOM_THREAD_POOL = new ThreadPoolExecutor(
    20, 30, 1, TimeUnit.MINUTES,
    new LinkedBlockingDeque<>(100), new ThreadPoolExecutor.DiscardPolicy()
);

...
    
final Stream<Integer> stream = Stream.of(1, 2, 3, 4);
Future<String> result = CUSTOM_THREAD_POOL.submit(() -> {
    return stream.map(String::valueOf).collect(Collectors.joining());
});
System.out.println(result.get());
```

#### 其他注意事项

##### 线程安全问题

由于并行流是多线程版的流，因此在使用并行流中修改共享变量时，需要特别注意线程安全问题。

##### 内存泄漏问题

由于并行流使用了线程池技术，如果在线程任务中使用了ThreadLocal，需要注意在线程任务执行完之后及时remove掉ThreadLocal的值，避免内存泄漏。

关于ThreadLocal在线程池中的内存泄漏问题，可以参考[这篇文章](https://www.jianshu.com/p/a1cd61fa22da)

##### 性能问题

并不是所有的操作使用并行流都能带来性能上的收益。stream的管道操作本身是否耗时，越耗时，并行流带来的收益越大，如果是CPU密集型操作，使用普通流也许更好，因为单线程能减少线程上下文切换以及合并各线程的流带来的额外开销。



### 3.6 关于流的其他建议和问题

#### 长Lambda表达式使用方法引用代替

流中使用的Lambda表达式尽量不要太多行，如果超过3行，最好提到一个方法中来引用：

如下面这种代码：

```java
// 不好的风格
Stream.of(1, 2, 3, 4)
    .map(i -> {
        int doubleVal = i * 2;
        String str = "The double value is: " + doubleVal;
        byte[] encodedData = Base64.getEncoder().encode(str.getBytes());
        return new String(encodedData, UTF_8);
    })
    .forEach(System.out::println);
```

可以替换成下面这样：

```java
// 推荐的风格
Stream.of(1, 2, 3, 4)
    .map(Other::convertAndEncode)
    .forEach(System.out::println);

public static String convertAndEncode(final Integer intVal) {
    int doubleVal = intVal * 2;
    String str = "The double value is: " + doubleVal;
    byte[] encodedData = Base64.getEncoder().encode(str.getBytes());
    return new String(encodedData, UTF_8);
}
```

#### List转换前的空检查

```java
List<Integer> list = null;
// 下面的代码做了空检查，因此不会抛空指针异常
// 前两行是模板，可以封装到工具类中
Optional.ofNullable(list)
    .orElseGet(Collections::emptyList)
    .stream()
    .map(String::valueOf)
    .forEach(System.out::println);
```

#### lambda抛出异常的问题

Lambda表达式实际上是一个实现了某个接口的匿名内部类，如果内部发生了checked异常，无法通过在外部函数声明throws来捕获，比如下面这个例子，就会报编译错误，提示IOException没有被捕获：

```java
public void exceptionThrow() throws IOException {
    Arrays.asList(1, 2, 3)
        .forEach(i -> {
            throw new IOException("message");
        });
}
```

很遗憾的是JDK并没有直接给我们提供一个向外抛出异常的解决方案，我们只能在lambda表达式内catch异常并处理，如果确实需要向外抛出异常，有3种解决方法：

1. 放弃使用Lambda表达式
2. 在Lambda表达式内部catch checked异常后，另外抛出一个运行时异常。
3. 使用适配模式，在Lambda外面再包装一层，并使用一种hack的手法绕过异常检查：

```java
// 声明一个能抛异常的Consumer函数式接口
// 这个接口对应于会抛出unchecked异常的Lambda表达式
@FunctionalInterface
private interface ConsumerWithException<T, E extends Exception> {
    void accept(T t) throws E;
}

// 将抛异常的Consumer包装成普通的Consumer返回，该方法本身会抛出异常E
private <T, E extends Exception> Consumer<T> 
    rethrowConsumer(ConsumerWithException<T, E> consumer) throws E {
    return t -> {
        try {
            consumer.accept(t);
        } catch (Exception exception) {
            throwAsUnchecked(exception);
        }
    };
}

// 将异常作为一个异常泛型抛出，该方法由于用到泛型，编译器无法确定是否是checked异常，因此可以绕过编译器的异常检查
@SuppressWarnings ("unchecked")
private static <E extends Throwable> void throwAsUnchecked(Exception exception) throws E {
    throw (E)exception;
}

// 改进后的带异常的lambda表达式
public void exceptionThrow() throws IOException {
    Arrays.asList(1, 2, 3)
        .forEach(rethrowConsumer(t -> {
            	throw new IOException("message");
        	})
        );
}
```

上面这种方式已经有人专门封装成一个工具类，可以参考[StackOverflow](https://stackoverflow.com/questions/27644361/how-can-i-throw-checked-exceptions-from-inside-java-8-streams)上查看该解决方案的完整代码



## 4. Optional

Optional是一个用来包装其他对象的类，顾名思义，被包装的对象是可能为空的对象。Optional提供了一些特殊的API来判断被包装对象是否为空，要获取被包装的对象值，我们可以通过一些结合了Lambda表达式和流式编程风格的API更优雅的处理空对象。

### 创建Optional包装对象

```java
// of的参数如果为空会抛出空指针异常
Optional<Integer> num1 = Optional.of(123);

// 创建一个空对象
Optional<Object> empty = Optional.empty();

// 如果需要包装的对象可能为空，则需要使用ofNullable
Optional<Object> num2 = Optional.ofNullable(null);
```

### 判断被包装对象是否为空

```java
// isPresent判断被包装的对象是否存在
assert false == Optional.empty().isPresent();

// ifPresent函数接受一个Consumer作为参数，用于处理被包装对象不为空时的值
// 当Optional的包装对象为空时，将什么都不会做
Optional.ofNullable("我不为空")
    .ifPresent(System.out::println);
```

### 获取被包装对象的值

```java
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
```

### 处理对象中嵌套属性的空判断

```java
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
```

> 说明：
>
> map方法会将Optional转换成另一个Optional，如果在流式处理中遇到一个为空，后面的所有map操作都会直接返回Optional.emtpy() ，不会去真正调用转换的方法，因此不会报空指针异常。



## 5. Map增强

Java8 为我们提供了一系列对Map接口的增加，通过lambda表达式的引入，简化了一些Map的常用操作，如：当key不存在时返回默认值，key不存在时才put等。下面通过一些例子来展示他们这些API的用法。

为了演示这些例子，先构造一个包含测试数据的Map：

```java
private static final Map<String, Integer> MAP = new HashMap<>();

static {
    MAP.put("a", 1);
    MAP.put("b", 2);
    MAP.put("c", 3);
    MAP.put("d", 4);
}
```

### forEach：循环遍历Map键值对

```java
// 遍历时，map的key和value将作为lambda表达式的参数
MAP.forEach((key, val) -> System.out.println("key = " + key + ", value = " + val));
```

### getOrDefault：key不存在时返回默认值

```java
// 当key不存在时，第二个参数作为默认值返回
assert 100 == MAP.getOrDefault("test", 100);

// 等价于下面的操作
assert 100 == (MAP.get("test") != null ? MAP.get("test") : 100 );
```

### putIfAbsent：key不存在时才put

该方法的返回值为，操作返回put之前key对应的值。

```java
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
```

### computIfAbsent: key不存在时计算一个新值put进去

该方法的返回值为，操作Map后这个key对应的值。

```java
// key对应的值不存在，就根据key计算一个值put到Map中，并返回这个值
assert 5 == MAP.computeIfAbsent("hello", key -> key.length());
assert 5 == MAP.get("hello");

// 等价于
if (MAP.get("hello") == null) {
    final int newValue = "hello".length();
    MAP.put("hello", newValue);
return newValue;
} else {
	return MAP.get("hello");
}
```

### computeIfPresent：key存在就计算一个新的值替换老值

该方法的返回值为，操作Map后这个key对应的值。

```java
assert 1 == MAP.get("a");
// 如果key存在就计算一个新的值替换老值
MAP.computeIfPresent("a", (oldKey, oldVal) -> oldVal * 2);
assert 2 == MAP.get("a");

// 等价于
if (MAP.get("a") != null) {
    MAP.put("a", MAP.get("a") * 2);
}
```

### merge 合并key的老值和指定值为一个新值设置进去

该方法的返回值为，操作Map后这个key对应的值。

```java
assert 2 == MAP.get("b");
// 合并key的老值和指定值为一个新值设置进去，put并返回，如果老值不存在就直接把给定值作为新值设置进去，不用进行合并
assert 5 == MAP.merge("b", 3, (oldVal, givenVal) -> oldVal + givenVal);
assert 5 == MAP.get("b");
```



## 6. CompletableFuture

### 6.1 什么是CompletableFuture？

在Java中CompletableFuture用于异步编程，异步编程是编写非阻塞的代码，运行的任务在一个单独的线程，与主线程隔离，并且会通知主线程它的进度，成功或者失败。

在这种方式中，主线程不会被阻塞，不需要一直等到子线程完成。主线程可以并行的执行其他任务。

使用这种并行方式，可以极大的提高程序的性能。

CompletableFuture实现了Future接口，因此Future有的特性它都有，它在Future的基础上进行了很多增强，让java8 可以更好的支持异步编程。

### 6.2 Java8以前的Future局限性

1. Future 本身是异步阻塞编程模型，不支持异步非阻塞的基于回调的编程模型，主线程需要通过Future.get()方法的阻塞等待子任务的结果。
2. 多个 Future 不能串联在一起组成链式调用 有时候你需要执行一个长时间运行的计算任务，并且当计算任务完成的时候，你需要把它的计算结果发送给另外一个长时间运行的计算任务等等。 你会发现你无法使用 Future 创建这样的一个工作流。 
3. Future API 没有任务的异常处理结构，异常会在异步任务内被处理掉。
4. 不能组合多个 Future 的结果。 假设你有10个不同的Future，你想并行的运行，然后在它们全部完成后或任意一个完成后，运行一些函数，你会发现你也无法使用 Future 这样做。

#### 传统的Future编程模型

```java
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
```



### 6.3 CompletableFuture的用法

#### 快速体验

在了解CompletableFuture之前，先写一个hello world程序，体验一下他的编程方式

```java
 // 在一个新线程中执行task
CompletableFuture.supplyAsync(() -> 100)
    // 获取到结果后将参数传给下一个函数，对结果进行转换
    .thenApply(Math::sqrt)
    // 然后将转换的结果给下一个Consumer消费
    .thenAccept(System.out::println);
```

#### CompletableFuture的创建

CompletableFuture的创建有三种方式，通过CompletableFuture的静态方法supplyAsync，runAsync以及completedFuture，分别对应的是有返回值，无返回值，和立即完成带返回值的异步任务。

```java
// 在一个新线程中执行一个有返回值的CompletableFuture
CompletableFuture<Integer> completableFuture1 = CompletableFuture.supplyAsync(() -> 100);

// 在一个新线程中执行一个无返回值的CompletableFuture
CompletableFuture<Void> completableFuture2 = CompletableFuture.runAsync(() -> System.out.println("task complete"));

// 创建一个已经完成的，有返回值的CompletableFuture
CompletableFuture<Integer> completableFuture3 = CompletableFuture.completedFuture(100);
```

#### 阻塞取值的API

由于CompletableFuture实现了Future接口，因此它也支持通过get方法阻塞的获取任务结果。其中getNow是CompletableFuture特有的。

```java
final CompletableFuture<Integer> completableFuture = CompletableFuture.supplyAsync(() -> 100);

// 阻塞获取结果
final Integer result = completableFuture.get();

// 阻塞获取结果，设置超时时间
final Integer result2 = completableFuture.get(2, TimeUnit.SECONDS);

// 立即获取结果，如果获取不到，返回参数中指定的默认值
final Integer result3 = completableFuture.getNow(0);
```

#### 任务链式处理

CompletableFuture最大的特点就是支持基于回调的链式编程风格，充分利用了Lambda表达式的便捷性，让多个任务的协作、依赖关系更清晰。

```java
// 在一个新线程中执行任务，获取结果
CompletableFuture.supplyAsync(() -> 3)
    // thenApply负责将上一个结果转化成另一个值
    .thenApply(result -> result * 2)
    // thenAccept负责消费上一步的结果
    .thenAccept(result -> System.out.println(result))
    // thenRun负责在上一步完成之后执行某些操作，它无法获取到上一步的结果
    .thenRun(() -> System.out.println("task complete"));
```

所有链式编程的API，如thenApply，thenAccept, thenRun, 以及后面会介绍的handle, whenComplete, exceptioinaly等方法都有其对应的异步版本。异步版本方法和普通方法的区别在于，异步方法会在一个新线程中执行任务，而非在调用者的线程中执行。

```java
// 异步版本，异步版本和同步版本的唯一区别就是异步的方法在新的线程中执行，而不是在上一步的线程中执行
CompletableFuture.supplyAsync(() -> 3)
    // 默认使用ForkJoinPool.commonPool
    .thenApplyAsync(result -> result * 2)
    // 这里使用自定义的线程池
    .thenAcceptAsync(result -> System.out.println(result), EXECUTOR_SERVICE)
    .thenRunAsync(() -> {
        System.out.println("task complete");
    });
```

#### 异常处理

CompletableFuture专门提供了异常处理的API，用于处理异步任务中抛出的异常，开发者不用再像以前用额外的方法去获取子任务中的异常。

CompletableFuture提供了3个方法：

##### handle(BiFunction<? super T, Throwable, ? extends U> fn)

无论是否有异常都会执行，需要内部判断，如果有异常需要提供一个默认值返回。

可以理解为带异常处理的thenApply。

```java
CompletableFuture.supplyAsync(CompletableUsage::exceptionTask)
    .handle((result, ex) -> {
        if (ex != null) {
            System.out.println(ex.getMessage());
            return defaultVal;
        }
        return result;
    })
    .thenAccept(result -> System.out.println("result is " + result));
```

##### whenComplete(BiConsumer<? super T, ? super Throwable> action)

无论是否有异常都会执行，需要内部判断，与handle不同的是，该方法只需要处理异常或结果，不带返回值。

可以理解为带异常处理的thenAccept，只消费，不产出。

```java
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
```

##### exceptionally(Function<Throwable, ? extends T> fn)

只有当有异常时才执行该方法，该方法需要处理异常，并返回发生异常时的默认返回值。

可以理解为带异常处理的thenApply，仅当有异常时执行。

```java
CompletableFuture.supplyAsync(CompletableUsage::exceptionTask)
    .exceptionally(ex -> {
        System.out.println(ex.getMessage());
        return defaultVal;
    })
    .thenAccept(result -> System.out.println("result is " + result));
```

#### CompletableFuture的组合

上面的例子都是一个CompletableFuture创建好后，通过函数是接口如Function，Consumer，Runnable等来编排多个行为的，如果现在有多个CompletableFuture，要让他们协同工作，比如多个CompletableFuture都完成时，或者只要有一个完成时就进行下一步操作，该怎么处理呢？这就需要用到CompletableFuture的组合API。

##### thenCompose

将两个CompletableFuture结合，第二个CompletableFuture依赖于第一个的执行结果。

```java
final CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> 100);
future.thenCompose(result -> createFromPreviousResult(result))
    .thenAccept(result -> System.out.println("result is " + result));
ThreadUtils.sleep(1000);
```

##### thenCombine

当两个CompletableFuture都完成之后再进行下一步调用。

```java
final CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(() -> 100);
final CompletableFuture<Integer> future2 = CompletableFuture.supplyAsync(() -> 200);
future1.thenCombine(future2, (result1, result2) -> result1 + result2)
    .thenAccept(result -> System.out.println("result is " + result));
```

##### allOf

多个任务都完成时，再进行下一步。

```java
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
```

##### anyOf

多个任务有一个完成时，就执行下一步，哪个先完成就得到哪个任务的结果。

```java
final List<CompletableFuture<Integer>> anyFutures = Arrays.asList(
    CompletableFuture.supplyAsync(() -> 100),
    CompletableFuture.supplyAsync(() -> 200),
    CompletableFuture.supplyAsync(() -> 300)
);
CompletableFuture.anyOf(anyFutures.toArray(new CompletableFuture[3]))
    .thenAccept(result -> System.out.println("result is " + result));
```



## 7. 新的日期/时间API

Java8 引入了一套新的时间API，位于java.time包内，这套API并不是老API的增强，而是完全重新实现了一套新的日期时间的类，用于替代被诟病多年的Date，Calendar，DateFormat等。

### 7.1 为什么需要新的日期/时间API

在Java 8之前，所有关于时间和日期的API都存在各种使用方面的缺陷，主要有:

1. Java的java.util.Date和java.util.Calendar类易用性差，不支持时区，由于所有的日期类都是可变的，因而他们都不是线程安全的；
2. 用于格式化日期的类DateFormat被放在java.text包中，它是一个抽象类，所以我们需要实例化一个SimpleDateFormat对象来处理日期格式化，并且DateFormat也是非线程安全，这意味着如果你在多线程程序中调用同一个DateFormat对象，会得到意想不到的结果;
3. 对日期的计算方式繁琐，而且容易出错，并且月份是从0开始的，从Calendar中获取的月份需要加一才能表示当前月份;

### 7.2 新日期/时间API的缺点

新的日期/时间API虽然提供了很多方便的操作，但是由于引入的新的概念和类比较多，理解上比老的API相对困难些，并且如果理解上有偏差（特别是对时区的理解），很容易在API调用报异常，并且往往这些异常都还是运行时异常（编译一口过，一跑全报错），因此，个人觉得新的API要能正确的使用好，还是有一定的门槛。

### 7.3 新日期/时间API中的一些概念

新的日期/时间API引入了很多新的类，分别代表了不同角度观察下的时间或其指标

**Instant**：时间戳，表示某个时间点，某个瞬间，是一个绝对时间，无时区的概念。同一个时间戳在不同时区下的时间不同。

**Clock**：Clock是一个带有时区的钟表抽象，通常用来获取当前时间。与Instant不同，Instant是一个固定的时间点，而Clock本身并不包含时间点，它是一个用来获取当前时区当前时间点的工具。

**ZoneId**：系统内置时区ID，比如东八区，用内置的id为"Asia/Shanghai"。

**ZoneOffset**：时区偏移量，继承自ZoneId，比如东八区，包含ZoneId信息和时区偏移量信息，偏移量为+08:00。

**Duration**：一段时间，基于时间的，例如23.5秒，2小时，5天，最大单位为天，最小单位为纳秒

**Period**：一段日子，基于日期的，例如5天，2个月，3年，最小单位为天，最大单位为年

**LocalDate**：本地日期，不包含时区

**LocalTime**：本地时间，不包含时区

**LocalDateTime**：本地日期时间，不包含时区

**ZonedDateTime**：包含时区的日期时间



了解了这些概念以后，接下来可以看看这些API的用法了。

### 7.4 新日期/时间API的使用

#### Instant的用法

##### 创建Instant

Instant时间戳，而时间戳表示的是一个绝对时间，无论当前系统处于哪个时区，时间戳都是同样的值。比如0时区的0点整和东八区的早上8点整，他们的时间戳都是相同的。因此***时间戳必须要从有时区的时间中获取***，让系统知道“2020-01-01 00:00:00”到底是以哪个时区的角度来观察的。

```java
// 获取当前时间戳
final Instant now = Instant.now();

// 从Date获取当前时间戳
final Instant instantFromDate = new Date().toInstant();
System.out.println(instantFromDate);

// 从Clock中获取当前时间戳
final Clock clock = Clock.systemDefaultZone();
final Instant instantFromClock = clock.instant();
System.out.println(instantFromClock);

// 从带时区的日期时间中获取
final Instant instantFromZonedDateTime = Instant.from(ZonedDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneId.of("Asia/Shanghai")));
assert "2019-12-31T16:00:00Z".equals(instantFromZonedDateTime.toString());

// 从LocalDateTime中获取（需要提供时区）
final Instant instantFromLocalDateTime = LocalDateTime.of(2020, 1, 1, 0, 0, 0).toInstant(ZoneOffset.ofHours(8));
assert "2019-12-31T16:00:00Z".equals(instantFromLocalDateTime.toString());
```

##### 获取时间戳的秒和毫秒

```java
final Instant now = Instant.now();
// 1970-01-01 00:00:00.000到时间戳的毫秒数
System.out.println(now.toEpochMilli());
// 1970-01-01 00:00:00到时间戳的秒数
System.out.println(now.getEpochSecond());
```

##### 与java.util.Date的互转

```java
final Instant now = Instant.now();
// Instant转Dte
final Date date = Date.from(now);
// Date转Instant
final Instant instant = date.toInstant();
```

##### 与其他新日期/时间类的转换

```java
final Instant now = Instant.now();
// Instant转LocalDateTime
final LocalDateTime localDateTime = LocalDateTime.ofInstant(now, ZoneId.systemDefault());
// Instant转ZonedDateTime
final ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(now, ZoneId.systemDefault());
```

#### Clock的用法

##### 创建Clock

```java
// 获取系统时区的钟
final Clock clockOfSystemDefaultZone = Clock.systemDefaultZone();
// 获取0时区的钟
final Clock clockOfUTCTimeZone = Clock.systemUTC();
// 获取指定时区的钟
final Clock clockOfSpecifyZone = Clock.system(ZoneId.of("Asia/Shanghai"));
```

##### 获取当前时间戳

```java
final Clock clock = Clock.systemDefaultZone();
// 获取当前时间戳
final Instant instant = clock.instant();
System.out.println(instant);
```

##### Clock的转换

```java
// 获取系统时区的钟
final Clock sysClock = Clock.systemDefaultZone();
// 获取比系统时区快一个小时的钟
final Clock offsetClock = Clock.offset(sysClock, Duration.ofHours(1));
System.out.println(sysClock.instant());
System.out.println(offsetClock.instant());

// 转换成系统时钟秒针滴答5次，他的秒针才滴答一次，但是一次滴答就前进5秒（第二个参数定义每次秒针滴答的间隔）
final Clock tickClock = Clock.tick(sysClock, Duration.ofSeconds(5));
try {
    for (int i=0; i < 16; i++) {
        System.out.println("sysClock: " + sysClock.instant());
        System.out.println("tickClock：" + tickClock.instant());
        Thread.sleep(1000);
    }
} catch (InterruptedException e) {
    e.printStackTrace();
}
```

#### 时区的获取

##### ZoneId的获取

```java
// 获取系统时区
final ZoneId systemDefaultZoneId = ZoneId.systemDefault();
final ZoneId systemDefaultZoneId2 = ZoneOffset.systemDefault();

// 获取指定ID的时区
final ZoneId specifyZoneId = ZoneId.of("Asia/Shanghai");
// 获取所有内置ZoneId字符串
final Set<String> availableZoneIds = ZoneId.getAvailableZoneIds();
// 从ZoneOffset获取ZoneId
ZoneOffset.ofHours(8); // 东八区
ZoneOffset.ofOffset("UTC", ZoneOffset.ofHours(8));
```

##### ZoneOffset的获取

```java
// 0时区
final ZoneOffset utc = ZoneOffset.UTC;
// 根据数字小时 +08:00偏移量
final ZoneOffset zoneOffset = ZoneOffset.ofHours(8);
// 根据时间分钟 +08:30偏移量
final ZoneOffset zoneOffset2 = ZoneOffset.ofHoursMinutes(8, 30);
// 根据字符串 +08:00偏移量
final ZoneOffset zoneOffset3 = ZoneOffset.of("+08:00");
```

#### 时间段

时间段分为基于日期的Period和基于时间的Duration

##### Duration创建

```java
// 创建1小时的间隔
final Duration oneHour = Duration.ofHours(1);

// 创建3天的间隔
final Duration threeDays = Duration.ofDays(3);

// 创建一分钟的间隔
final Duration oneMinutes = Duration.of(1, ChronoUnit.MINUTES);

// 根据起始时间计算时间间隔
final Duration towTimeDuration = Duration.between(
    LocalDateTime.of(2020, 1, 1, 8, 0, 0),
    LocalDateTime.of(2020, 1, 1, 10, 0, 0)
);
assert 2 == towTimeDuration.toHours();
```

##### Duration转换

```java
// 获取1小时+30分钟-20秒的时间间隔
final Duration duration = Duration.ofHours(1)
    .plus(30, ChronoUnit.MINUTES)
    .minus(20, ChronoUnit.SECONDS);

// 时间间隔转换成分钟数
final long minutes = duration.toMinutes();
// 时间间隔转换成纳秒
final long nanos = duration.toNanos();
```

##### Period创建

```java
// 创建1年2月3天的日期间隔
final Period period1 = Period.of(1, 2, 3);
// 创建30天的日期间隔
final Period period2 = Period.ofDays(30);
// 根据起止日期计算间隔
final Period period3 = Period.between(
    LocalDate.of(2020, 1, 14),
    LocalDate.of(2020, 1, 1)
);
```

##### Period转换

```java
// 创建1年+2个月+3天的日期间隔
final Period period = Period.ofYears(1).plusMonths(2).plusDays(3);
// 两个Period计算：1个月减15天
final Period period2 = Period.ofMonths(1).minus(Period.ofDays(15));
```

#### LocalDate用法

LocalDate只包含年月日，不包含时间，也不包含时区，能表示的信息很有限，但是计算API非常丰富。

##### LocalDate的创建

```java
// 获取当前日期
final LocalDate currentDate = LocalDate.now();
// 获取指定日期 2020年1月2日
final LocalDate date1 = LocalDate.of(2020, 1, 2);
// 获取某年的第几天：2020年的第32天（2月1日）
final LocalDate date2 = LocalDate.ofYearDay(2020, 32);
        
// 从LocalDateTime提取
final LocalDate fromLocalDateTime1 = LocalDate.from(LocalDateTime.now());
final LocalDate fromLocalDateTime2 = LocalDateTime.now().toLocalDate();

// 从字符串解析出来
final LocalDate parsedDate1 = LocalDate.parse("2020-01-01");
final LocalDate parsedDate2 = LocalDate.parse("2020-01-01", FORMATTER);
```

##### LocalDate的计算

```java
//当前日期加上1天再减去一个月
final LocalDate date1 = LocalDate.now()
    .plusDays(1)
    .minus(2, ChronoUnit.MONTHS);

// 判断当前日期所在年份是否是润年
final boolean leapYear = LocalDate.now().isLeapYear();
System.out.println(leapYear);

// isBefore isAfter 判断日期先后
assert LocalDate.now().isAfter(LocalDate.of(2020, 1, 1));
assert LocalDate.of(2020, 1, 1).isBefore(LocalDate.now());

// 转字符串
assert "2020-01-01".equals(LocalDate.of(2020, 1, 1).format(DateTimeFormatter.ISO_LOCAL_DATE));
assert "01/01/2020".equals(LocalDate.of(2020, 1, 1).format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));

// 调整日期
assert LocalDate.of(2020, 1, 1)
    .with(ChronoField.MONTH_OF_YEAR, 2) // 修改月份为2月
    .with(TemporalAdjusters.next(DayOfWeek.FRIDAY)) // 下个周五
    .with(TemporalAdjusters.lastDayOfMonth()) // 当月的最后一天
    .equals(LocalDate.of(2020, 2, 29));

// 获取某个字段的取值范围，上面的TemporalAdjusters.lastDayOfMonth()就是用到了这个range方法
// 比如2020年2月的日期范围
final ValueRange range = LocalDate.of(2020, 2, 1).range(ChronoField.DAY_OF_MONTH);
// 2020年的2月的日期，最大值是29，因为润年有29天
assert 29 == range.getMaximum();
// 2020年2月的日期，最小值是1
assert 1 == range.getMinimum();

// 转换成LocalDateTime
final LocalDateTime localDateTime = LocalDate.now().atStartOfDay();
final LocalDateTime localDateTime1 = LocalDate.now().atTime(8, 30, 5);

// 转换成带有时区信息的ZonedDateTime
final ZonedDateTime zonedDateTime = LocalDate.now().atStartOfDay(ZoneId.systemDefault());
final ZonedDateTime zonedDateTime1 = LocalDate.now()
            .atTime(LocalTime.of(8, 30, 5))
            .atZone(ZoneId.systemDefault());
```

> 注意：所有计算，包括后面的LocalTime，LocalDateTime等，都不会改变原有对象的值，而是计算后返回一个新的时间对象。
>
> 正是因为这些日期/时间对象的不变性，才使得他们能保证线程安全。

#### LocalTime用法

LocalTime只包含时间，不包含时间，也不包含时区。

##### LocalTime创建

```java
// 当前时间
LocalTime.now();
// 时分 01:02
LocalTime.of(1, 2);
// 时分秒 01:02:03
LocalTime.of(1, 2, 3);
// 时分秒纳秒 01:02:03.000000800
LocalTime.of(1, 2, 3, 800);
// 一天的第300秒的时间
LocalTime.ofSecondOfDay(300);
// 从LocalDateTime提取时间
LocalTime.from(LocalDateTime.now());
LocalDateTime.now().toLocalDate();
// 从字符串解析
LocalTime.parse("08:00:00");
LocalTime.parse("08:00:00", TIME_FORMATTER);
```

##### LocalTime的计算

```java
final LocalTime time = LocalTime.of(8, 0, 0);

assert "08:30:00".equals(
    time.plusHours(1) // 增加一个小时
    .minus(30, ChronoUnit.MINUTES) // 减30分钟
    .format(TIME_FORMATTER) // 按照指定格式，格式化成字符串
);

// 判断时间先后
assert time.isAfter(LocalTime.of(7, 59, 59));
assert LocalTime.of(7, 59, 59).isBefore(time);

// 调整时间
assert 40 == time.with(ChronoField.MINUTE_OF_HOUR, 20) // 分钟调整为20分
    .withHour(9) // 小时调整为9点
    .until(LocalTime.of(10, 0, 0), ChronoUnit.MINUTES); // 等多少分钟到10点

// 计算时间差
assert 1 == Duration.between(time, LocalTime.of(9, 0, 0)).toHours();

// 与LocalDateTime和ZonedDateTime的转换
final ZonedDateTime zonedDateTime = time
    .atDate(LocalDate.of(2020, 1, 1)) // 加上LocalDate，转换成LocalDateTime
    .atZone(ZoneId.systemDefault());// 再加上时区，转换成ZonedDateTime
```

#### LocalDateTime用法

LocalDateTime即包含了日期，又包含了时间，但是仍然不包含时区

##### LocalDateTime的创建

```java
// 当前日期时间
LocalDateTime.now();
// 获取当前时间对应的UTC 0时区的时间
LocalDateTime.now(ZoneOffset.UTC);
// 根据时间戳和时区，得到LocalDateTime的表示（这里会比当前时间小8小时）
LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);
// 通过年月日时分秒创建 2020-01-01 08:00:00
LocalDateTime.of(2020, 1, 1, 8, 0, 0);
// 通过LocalDate和LocalTime组合
LocalDateTime.of(LocalDate.now(), LocalTime.now());
//从字符串解析
LocalDateTime.parse("2020-01-01T08:00:00");
// 使用自定义DateTimeFormatter格式化
LocalDateTime.parse("2020-01-01T08:00:00", DATE_TIME_FORMATTER);
// 使用内置DateTimeFormatter格式化
LocalDateTime.parse("2020-01-01T08:00:00", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
```

##### LocalDateTime的计算

```java
final LocalDateTime dateTime = LocalDateTime.of(2020, 1, 1, 8, 0, 0);
// 提取日期部分到LocalDate
final LocalDate localDate = dateTime.toLocalDate();
// 提取时间部分到LocalTime
final LocalTime localTime = dateTime.toLocalTime();
// 转换成时间戳
final Instant instant = dateTime.atZone(ZoneId.systemDefault()).toInstant();

// 日期时间加减
assert "2021-02-02T08:59:01".equals(
dateTime.plusYears(1)
.plusMonths(1)
.plusDays(1)
.plusHours(1)
.minusMinutes(1)
.plusSeconds(1)
.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
);

// 计算到某个时间还有多少小时
assert 1 == dateTime.until(
LocalDateTime.of(2020, 1, 1, 9, 0, 0),
ChronoUnit.HOURS
);

// 截断到天，时分秒都变为0
assert "2020-01-01T00:00:00".equals(
dateTime.truncatedTo(ChronoUnit.DAYS)
.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
);

// 转换为带时区的对象
final ZonedDateTime zonedDateTime = dateTime.atZone(ZoneId.systemDefault());
// 转换为某个时区的Date
final Date date = Date.from(
    dateTime.atZone(ZoneId.systemDefault()).toInstant()
);
```

> 由于LocalDate，LocalTime有的操作LocalDateTime也绝大多数都支持，所以这里就不一一列举了，可以下来看API。

#### ZonedDateTime

ZonedDateTime是LocalDateTime的基础上加了时区，因此可以准确的表达为某个时区的时间，可以转换成Instant，Date等带时区的对象，也可以提取出LocalDateTime，LocalDate，LocalTime等不带时区的对象，所有的时间计算操作和LocalDateTime也类似。

##### ZonedDateTime的创建

```java
// 当前时区的当前时间
ZonedDateTime.now();
// 零时区的当前时间
ZonedDateTime.now(ZoneOffset.UTC);
// 从LocalDateTime和时区创建
ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault());
// 从LocalDate，LocalTime和时区创建
ZonedDateTime.of(LocalDate.now(), LocalTime.now(), ZoneId.systemDefault());
// 从年月日时分秒纳秒和时区创建
ZonedDateTime.of(2020, 1, 1, 8, 0, 0, 0, ZoneId.systemDefault());
```

##### ZonedDateTime的计算

```java
final ZonedDateTime zonedDateTime = ZonedDateTime.of(2020, 1, 1, 8, 0, 0, 0, ZoneId.systemDefault());
// 转换成时间戳
zonedDateTime.toInstant();
// 转换成Date
Date.from(zonedDateTime.toInstant());
// 加减计算
zonedDateTime.plusDays(30)
    .minus(1, ChronoUnit.DAYS)
    .plusMonths(1);
// 格式化成字符串
zonedDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

// 提取LocalDateTime，LocalDate, LocalTime和时区
final LocalDateTime localDateTime = zonedDateTime.toLocalDateTime();
final LocalDate localDate = zonedDateTime.toLocalDate();
final LocalTime localTime = zonedDateTime.toLocalTime();
final ZoneId zone = zonedDateTime.getZone();

// 时间表示不变，只切换时区，把东八区的1月1号8点变成零时区的1月1号8点
assert "2020-01-01T08:00Z".equals(zonedDateTime.withZoneSameLocal(ZoneOffset.UTC).toString());
// 时间戳不变，改时区，同一个时间戳在不同时区的时间是不痛的，因此，会有时区转换，东八区的时间戳在零时区要少8个小时
assert "2020-01-01T00:00Z".equals(zonedDateTime.withZoneSameInstant(ZoneOffset.UTC).toString());
```

> 由于LocalDate，LocalTime，LocalDateTime有的操作ZonedDateTime也绝大多数都支持，所以这里就没一一列举了，可以下来看API。