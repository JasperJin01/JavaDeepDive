package org.study.juc.p03_syn;


import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * conditionA.await() 会释放当前线程持有的锁，并将线程挂起，直到它被唤醒。
 * 被唤醒后，线程必须重新争夺锁，直到它能够成功获取锁并继续执行。
 * Condition.await() 和 Object.wait() 的一个显著区别是，await() 需要和 ReentrantLock 配合使用，
 * 而 wait() 是在对象级别的同步中使用的。
 *
 * fixme 问题1: 为什么 conditionA.await() 要用 while 而不是 if?
 * 解释是说，当await被唤醒后，需要重新争夺锁，且state不一定是1，因此需要用while
 * 感觉并发编程的时候，这种获取锁的操作，很少用if，大多都用while
 *
 * fixme 问题2: 为什么 state 不需要 volatile?
 * 这里的 ReentrantLock 和 Condition 已经确保了线程之间的同步和内存可见性，因此不需要额外使用 volatile。
 * volatile 适用于 简单的共享状态，例如 标志位，并且能够保证变量的可见性和禁止重排序。 例如 volatile boolean flag = false;
 */
class ShareData {
    private int state = 1; // 控制顺序 1-A, 2-B, 3-C
    private final Lock lock = new ReentrantLock(); // 保证操作的原子性和线程间的互斥访问
    // 实现不同线程的等待和唤醒机制
    private final Condition conditionA = lock.newCondition();
    private final Condition conditionB = lock.newCondition();
    private final Condition conditionC = lock.newCondition();

    public void printA() {
        lock.lock(); // 获取锁
        try {
            while (state != 1) conditionA.await(); // 释放锁，并等待
//            if (state != 1) conditionA.await();
            System.out.print("A");
            state = 2; // 修改状态，使得B可以打印
            conditionB.signal(); // 唤醒B线程
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }

    public void printB() {
        lock.lock();
        try {
            while (state != 2) conditionB.await(); // 释放锁，并等待
//            if (state != 2) conditionB.await();

            System.out.print("B");
            state = 3; // 修改状态，使得C可以打印
            conditionC.signal(); // 唤醒C线程
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }

    public void printC() {
        lock.lock();
        try {
            while (state != 3) conditionC.await(); // 释放锁，并等待
//            if (state != 3) conditionC.await();

            System.out.print("C");
            state = 1; // 修改状态，使得A可以打印
            conditionA.signal(); // 唤醒A线程
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }
}

/**
 * 顺序打印ABC
 */
public class ABC {
    public static void main(String[] args) {
        for (int r = 0; r < 10; r++) {
            ShareData shareData = new ShareData();

            // 创建线程
            Thread threadA = new Thread(() -> {
                for (int i = 0; i < 10; i++) {
                    shareData.printA();
                }
            }, "A");

            Thread threadB = new Thread(() -> {
                for (int i = 0; i < 10; i++) {
                    shareData.printB();
                }
            }, "B");

            Thread threadC = new Thread(() -> {
                for (int i = 0; i < 10; i++) {
                    shareData.printC();
                }
            }, "C");

            // 启动线程
            threadA.start();
            threadB.start();
            threadC.start();


            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            System.out.println();

        }


    }


}






