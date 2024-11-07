package org.study.juc.p05_util;

import java.util.Random;
import java.util.concurrent.Semaphore;

public class D03_Semaphore {

    public static void main(String[] args) {
        Semaphore semaphore = new Semaphore(3); // 3: 资源数

        for (int i = 0; i < 6; i++) {
            new Thread(() -> {

                try {
                    semaphore.acquire(); // 获取资源：如果没有空余资源可获取，当前方法阻塞
                    System.out.println(Thread.currentThread().getName() + "抢到了车位");
                    Thread.sleep(new Random().nextInt(1000));
                    System.out.println(Thread.currentThread().getName() + "离开了车位 bye~");
                    semaphore.release(); // 释放资源：资源一旦被释放，阻塞的线程就会继续执行
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }, "car" + (i + 1)).start();
        }

    }


}
