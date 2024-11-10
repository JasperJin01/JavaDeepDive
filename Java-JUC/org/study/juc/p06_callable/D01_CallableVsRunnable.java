package org.study.juc.p06_callable;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;


/**
 * @FunctionalInterface
 * public interface Callable<V> {
 *     V call() throws Exception;
 * }
 *
 * @FunctionalInterface
 * public interface Runnable
 *     public abstract void run();
 * }
 *
 * 对比不同：
 * 1. Callable 比 Runnable 多一个泛型的返回值
 * 2. Runnable 没有抛出异常，所以在执行例如 Thread.sleep() 操作只能 try-catch，不能抛出异常
 */
class MyRunnable implements Runnable {
    @Override
    public void run() {
        System.out.println("MyRunnable");
    }
}

class MyCallable implements Callable<Integer> {
    @Override
    public Integer call() throws Exception {
        Thread.sleep(3230);
        System.out.println("MyCallable");
        return 100;
    }
}

public class D01_CallableVsRunnable {


    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // Runnable 创建线程
        new Thread(new MyRunnable(), "t1").start();

        // Callable 创建线程
        // FutureTask继承自Runnable (FutureTask<-RunnableFuture<-Runnable, Future)，同时接收Callable作为属性
        FutureTask<Integer> futureTask = new FutureTask<>(new MyCallable());
        new Thread(futureTask, "t2").start();

        System.out.println("futureTask.get() 正在阻塞");

        // 获取返回值
        Integer ret = futureTask.get();
        System.out.println("ret = " + ret);

        Integer ret2 = futureTask.get(); // 又get了一遍计算结果（并不会多次调用线程，即线程只执行一次）
        System.out.println("ret2 = " + ret2);

        // 调用get会阻塞主线程！
        System.out.println("main");
    }

}


class Test {
    /**
     * 一个FutureTask，两个Thread
     */
    public static void main(String[] args) {
        // 多个线程使用同一个 FutureTask 对象，第一个被触发的线程执行call方法，其他线程共享结果
        FutureTask<Integer> futureTask = new FutureTask<>(new MyCallable());
        new Thread(futureTask, "t1").start();
        new Thread(futureTask, "t2").start();

//        MyRunnable myRunnable = new MyRunnable();
//        new Thread(myRunnable, "t4").start();
//        new Thread(myRunnable, "t4").start();
    }
}

/**
 * NOTE 函数式接口：函数式接口指的是仅包含一个抽象方法的接口，这种接口可以用作Lambda表达式或方法引用的目标
 * @see org.study.syntax.lambda.FunctionalInterface
 */