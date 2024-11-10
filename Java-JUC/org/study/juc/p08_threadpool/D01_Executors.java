package org.study.juc.p08_threadpool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class D01_Executors {

    public static void main(String[] args) {
        /**
         * 阿里开发手册规范：
         * 【强制】线程池不允许使用 Executors 去创建，而是通过 ThreadPoolExecutor 的方式，这
         *  样的处理方式让写的同学更加明确线程池的运行规则，规避资源耗尽的风险。
         *  --------------------------------------
         *  FixedThreadPool 和 SingleThreadPool：
         *    允许的请求队列（LinkedBlockingQueue）长度为 Integer.MAX_VALUE，可能会堆积大量的请求，从而导致 OOM。
         *  CachedThreadPool：
         *    允许的创建线程数量为 Integer.MAX_VALUE，可能会创建大量的线程，从而导致 OOM。
         */
        // 只要有任务，就有线程 底层是「同步队列」synQueue
        ExecutorService threadPool = Executors.newCachedThreadPool();

//        ExecutorService threadPool = Executors.newFixedThreadPool(3);

//        ExecutorService threadPool = Executors.newSingleThreadExecutor();

        for (int i = 0; i < 1000; i++) {

            threadPool.execute(() -> {
                System.out.println(Thread.currentThread().getName() + " 执行任务");

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            });

        }
        // 释放线程池对象
        threadPool.shutdown();
    }

    static class Scheduled {
        // 定时任务
        // 延时执行？定时执行？这都是啥
        public static void main(String[] args) {
            ScheduledExecutorService pool = Executors.newScheduledThreadPool(3);

        }
    }

}


