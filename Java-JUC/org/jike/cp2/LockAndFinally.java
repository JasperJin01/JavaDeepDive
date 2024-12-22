package org.jike.cp2;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 对 lockInterruptibly 的测试
 */
public class LockAndFinally {
    private static final Lock lockA = new ReentrantLock();
    private static final Lock lockB = new ReentrantLock();

    public static void main(String[] args) throws InterruptedException {

        Thread thread1 = new Thread(() -> {
            try {
                lockA.lockInterruptibly();
                try {
                    System.out.println("Thread 1: Holding lock A...");
                    Thread.sleep(100); // 模拟操作
                    lockB.lockInterruptibly();
                    try {
                        System.out.println("Thread 1: Acquired lock B!");
                    } finally {
                        System.out.println("Thread 1: 对 lockB 解锁");
                        lockB.unlock();
                    }
                } finally {
                    System.out.println("Thread 1: 对 lockA 解锁");
                    lockA.unlock();
                }
            } catch (InterruptedException e) {
                System.out.println("Thread 1: Interrupted while waiting for locks");
            }
        });

        // fixme 这个代码有问题，要向上面的thread1 那样写
        Thread thread2 = new Thread(() -> {
            try {
                lockB.lockInterruptibly();
                System.out.println("Thread 2: Holding lock B...");
                Thread.sleep(100); // 模拟操作
                lockA.lockInterruptibly();
                System.out.println("Thread 2: Acquired lock A!");
                lockA.unlock();
                lockB.unlock();
            } catch (InterruptedException e) {
                System.out.println("Thread 2: Interrupted while waiting for locks");
            }
        });

        thread1.start();
        thread2.start();

        // 让两个线程开始执行一段时间，模拟死锁的情形
        Thread.sleep(50);

        // 发送中断信号，打破死锁
        thread1.interrupt();
//        thread2.interrupt();
    }
}

/**
 * finally 中抛出的异常会覆盖 try 中抛出的异常，因此你只会看到 finally 中的异常信息。
 */
class FinallyExceptionExample {
    public static void main(String[] args) {
        try {
            System.out.println("In try block");
            throw new RuntimeException("Exception in try");
        } finally {
            System.out.println("In finally block");
            throw new RuntimeException("Exception in finally");
        }
    }
}
