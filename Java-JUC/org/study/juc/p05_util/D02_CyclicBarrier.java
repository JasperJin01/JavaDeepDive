package org.study.juc.p05_util;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class D02_CyclicBarrier {
    public static void main(String[] args) {
//        CyclicBarrier cyclicBarrier = new CyclicBarrier(3); // 3: 3个线程

        /**
         * CyclicBarrier的另一种实现方法
         */
        CyclicBarrier cyclicBarrier = new CyclicBarrier(3, () -> {
            System.out.println(Thread.currentThread().getName() + "发生了某件事-----------------------------");
            // 最后一个到达屏障点的线程会执行action动作
            // 这里可以对屏障点进行一个收尾/统计工作
        });


        for (int i = 0; i < 3; i++) {
            new Thread(() -> {

                try {

                    System.out.println(Thread.currentThread().getName() + "开始第一关");
                    Thread.sleep(new Random().nextInt(3000));
                    System.out.println(Thread.currentThread().getName() + "通过第一关，等待其他人");

                    cyclicBarrier.await(); // 线程会在这里阻塞，必须等待三个线程都执行完毕之后，才会继续执行
                    // 有点像双人行游戏的屏障点/里程碑，一起达到里程碑后，一起进入下一个步骤

                    System.out.println(Thread.currentThread().getName() + "开始第二关");
                    Thread.sleep(new Random().nextInt(3000));
                    System.out.println(Thread.currentThread().getName() + "通过第二关，等待其他人");


                    cyclicBarrier.await();

                    System.out.println(Thread.currentThread().getName() + "开始第三关");
                    Thread.sleep(new Random().nextInt(3000));
                    System.out.println(Thread.currentThread().getName() + "通过第三关，等待其他人");

                    cyclicBarrier.await();


                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (BrokenBarrierException e) {
                    throw new RuntimeException(e);
                }
            }, "进程 " + (i + 1)).start();
        }

        System.out.println("111111"); // 主线程和子线程没关系！


    }
}
