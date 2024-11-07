package org.study.juc.p03_syn;


import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class ShareData {
    private int state = 1; // 控制顺序 1-A, 2-B, 3-C
    private final Lock lock = new ReentrantLock();
    private final Condition conditionA = lock.newCondition();
    private final Condition conditionB = lock.newCondition();
    private final Condition conditionC = lock.newCondition();

    public void printA() {
        lock.lock();
        try {
            while (state != 1) {
                conditionA.await();
            }
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
            while (state != 2) {
                conditionB.await();
            }
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
            while (state != 3) {
                conditionC.await();
            }
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
    }

}





