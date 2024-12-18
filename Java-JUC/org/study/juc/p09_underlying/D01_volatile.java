package org.study.juc.p09_underlying;

import java.util.stream.IntStream;

public class D01_volatile {
//    private static Integer flag = 1;
    // 使用 volatile 可以确保共享变量的可见性。当一个变量被声明 volatile 时，每次访问该变量
    // 都会从主内存中读取最新的值，并且对该变量的修改会立即写入主内存，而不是先写入缓存
    // 这样可以确保当一个线程修改了 volatile 变量后，其他线程立刻看到最新的值
    private static volatile Integer flag = 1;

    public static void main(String[] args) throws InterruptedException {
        new Thread(() -> {
            System.out.println("子线程 flag = " + flag);

            while (flag == 1) ;

            System.out.println("子线程 新的 flag = " + flag); // 不使用volatile，子线程看不到主线程的 2

        }).start();

        Thread.sleep(500);
        flag = 2;

        System.out.println("主线程 flag = " + flag);

    }
}


// volatile 不能解决原子性问题
class Test {
    static Integer count = 0;
    // Integer 是不可变对象：在 Java 中，Integer 是不可变的，每次对 count 的修改（如 count++）都会生成一个新的 Integer 对象。
    // 也就是说，当 count++ 执行时，count 会被赋予一个新的对象引用，而不是在原来的对象上进行修改。

    public static void main(String[] args) throws InterruptedException {
        Object lock = new Object();
        new Thread(() -> {
            for (int i = 0; i < 5000; i++) {
//                synchronized (count) { // 错的
                synchronized (lock) {
                    count++;
                }
            }
        }).start();

        new Thread(() -> {
            for (int i = 0; i < 5000; i++) {
                synchronized (lock) {
                    count++;
                }
            }
        }).start();

        Thread.sleep(1000);
        System.out.println("count = " + count);

    }
}
