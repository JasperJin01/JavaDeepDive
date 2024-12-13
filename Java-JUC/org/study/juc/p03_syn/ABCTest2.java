package org.study.juc.p03_syn;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 这段代码只用了一个condition做条件，能够打印出123123...，不知道代码是否有问题
 * fixme 关于 signal 和 signalAll, 不太理解为什么使用signal，即便是唤醒的线程不满足state，
 *  仍然可以执行。为什么？？？
 */
class ShareData2 {
    private int state = 2;
    private final Lock lock = new ReentrantLock();
    private final Condition c = lock.newCondition();

    public void printA() {
        lock.lock();
        try {
            while (state != 1) c.await();
            System.out.print("1");
            state = 2;
            c.signal();
//            c.signalAll();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    public void printB() {
        lock.lock();
        try {
            while (state != 2) c.await();
            System.out.print("2");
            state = 3;
            c.signal();
//            c.signalAll();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    public void printC() {
        lock.lock();
        try {
            while (state != 3) c.await();
            System.out.print("3");
            state = 1;
            c.signal();

//            c.signalAll();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }



}

public class ABCTest2 {

    public static void main(String[] args) {
        ShareData2 shareData = new ShareData2();
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
