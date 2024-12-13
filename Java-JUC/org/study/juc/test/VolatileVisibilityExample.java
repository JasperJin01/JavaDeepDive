package org.study.juc.test;

public class VolatileVisibilityExample {
    private volatile boolean flag = false;

    public void thread1() {
        // 线程1修改 flag
        System.out.println("Thread 1: Setting flag to true.");
        flag = true;  // 修改 flag 的值
    }

    public void thread2() {
        // 线程2检查 flag，直到 flag 为 true
        while (!flag) {
            // 一直等待 flag 被设置为 true
        }
        System.out.println("Thread 2: Flag is true, exiting loop.");
    }

    public static void main(String[] args) throws InterruptedException {
        VolatileVisibilityExample example = new VolatileVisibilityExample();

        // 创建两个线程
        Thread t1 = new Thread(example::thread1);
        Thread t2 = new Thread(example::thread2);

        t1.start();
        t2.start();

        // 等待线程1和线程2执行完毕
        t1.join();
        t2.join();
    }
}
