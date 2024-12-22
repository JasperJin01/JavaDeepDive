package org.jike.cp2.locktest;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 示例场景：打印任务的调度
 * NOTE 不要把lock.lock()写在try里，必须获取锁后才能unlock，否则会抛出异常 IllegalMonitorStateException
 * 正确的获取锁的方式:
 *  class X {
 *    private final ReentrantLock lock = new ReentrantLock();
 *    // ...
 *
 *    public void m() {
 *      lock.lock();  // block until condition holds
 *      try {
 *        // ... method body
 *      } finally {
 *        lock.unlock()
 *      }
 *    }
 *  }
 *
 */
public class LockInterruptibly {

}

class Printer {
    private final Lock lock = new ReentrantLock();

    // 打印任务
    public void printJob(String document) {
        try {
            // 使用 lockInterruptibly 获取锁
            lock.lockInterruptibly();
            try {
                // 模拟打印过程
                System.out.println(Thread.currentThread().getName() + " 开始打印: " + document);
                Thread.sleep(2000);  // 假设打印需要2秒
                System.out.println(Thread.currentThread().getName() + " 打印完成: " + document);
            } finally {
                lock.unlock();  // 确保释放锁
            }
        } catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName() + " 被中断，无法完成打印！");
        }
    }


}

class PrinterTaskExample {
    public static void main(String[] args) throws InterruptedException {
        Printer printer = new Printer();

        // 创建两个打印任务线程
//        Thread thread1 = new Thread(() -> printer.printJob1("Document1"), "thread1");
//        Thread thread2 = new Thread(() -> printer.printJob1("Document2"), "thread2");
        Thread thread1 = new Thread(() -> printer.printJob("Document1"), "thread1");
        Thread thread2 = new Thread(() -> printer.printJob("Document2"), "thread2");

        // 启动线程
        thread1.start();
        thread2.start();

        // 等待一段时间后中断线程2
        Thread.sleep(500);  // 等待一段时间，确保 thread2 在打印时被中断
        thread2.interrupt();

        // 等待线程结束
        thread1.join();
        thread2.join();
    }
}