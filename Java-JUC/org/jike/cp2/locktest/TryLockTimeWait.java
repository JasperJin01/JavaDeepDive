package org.jike.cp2.locktest;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * LockInterruptibly 可能在已经获取锁后被中断，
 * TryLock(long time, TimeUnit unit) 可能在等待获取锁的过程中被中断（但获取到锁之后不会被中断）
 */
public class TryLockTimeWait {
}


class Database {
    private final Lock lock = new ReentrantLock();

    // 访问数据库的方法
    public void accessDatabase(String threadName) {
        try {
            // 尝试在 1 秒内获取锁
            if (lock.tryLock(1, TimeUnit.SECONDS)) {
                try {
                    // 模拟数据库访问
                    System.out.println(threadName + " 获取锁并正在访问数据库...");
                    Thread.sleep(2000);  // 假设数据库操作需要2秒
                    System.out.println(threadName + " 完成数据库操作。");
                } finally {
                    lock.unlock();  // 确保释放锁
                }
            } else {
                // 如果没有获取到锁，输出未获取锁的信息
                System.out.println(threadName + " 无法获取锁，稍后再试。");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println(threadName + " tryLock 被中断！");
        }
    }
}

class TryLockExample {
    public static void main(String[] args) throws InterruptedException {
        Database database = new Database();

        // 创建多个线程尝试访问数据库
        Thread thread1 = new Thread(() -> database.accessDatabase("Thread 1"));
        Thread thread2 = new Thread(() -> database.accessDatabase("Thread 2"));
        Thread thread3 = new Thread(() -> database.accessDatabase("Thread 3"));

        // 启动线程
        thread1.start();
        thread2.start();
        thread3.start();

        // 等待线程结束
        thread1.join();
        thread2.join();
        thread3.join();
    }
}
