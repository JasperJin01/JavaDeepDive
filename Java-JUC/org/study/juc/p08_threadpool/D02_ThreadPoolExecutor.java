package org.study.juc.p08_threadpool;

import java.util.concurrent.*;

public class D02_ThreadPoolExecutor {

    public static void main(String[] args) {
        // 自定义的线程池
        ThreadPoolExecutor pool = new ThreadPoolExecutor(
                2, // 核心线程数
                5, // 最大线程数
                2, // 当到达一定时间后，核心线程之外的线程将会被回收
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(3), // 有界队列
                Executors.defaultThreadFactory(), // TODO 这个是干啥的？？
                // 拒绝策略：当前线程池中，能同时并行处理的任务=最大线程5+阻塞队列3=8，
                // 当第9个任务到来的时候，就会执行拒绝策略
//                new ThreadPoolExecutor.AbortPolicy() // 丢弃任务并抛出异常
//                new ThreadPoolExecutor.CallerRunsPolicy() // 调用者运行 也就是主线程执行了
//                new ThreadPoolExecutor.DiscardOldestPolicy() // 抛弃等待最久的任务
//                new ThreadPoolExecutor.DiscardPolicy() // 丢弃任务
                new RejectedExecutionHandler() {
                    @Override
                    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                        System.out.println("拒绝");
                    }
                }
        );

        try {
            for (int i = 0; i < 9; i++) {
                int finalI = i; // FIXME 为什么在lamba表达式中，直接用 i 就不行， finalI = i 然后用 finalI 就行？
                // TODO execute 和 submit的区别
                pool.execute(() -> {
                    System.out.println(Thread.currentThread().getName() + " 执行任务 " + finalI);
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        } finally {
            pool.shutdown();
        }

    }
}