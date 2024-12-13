package org.study.juc.p05_util;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * CountDownLatch 对象，初始计数器值为 6，表示需要等待 6 个线程完成，这里表示教室里还有6个同学
 * 创建的六个 thread 代表六个同学，他们分别经过一段时间后离开教室（调用countDownLatch.countDown）
 * countDownLatch.await会阻塞 直到减为0才执行
 */
public class D01_CountDownLatch {

    public static void main(String[] args) {
        CountDownLatch countDownLatch = new CountDownLatch(6);

        for (int i = 0; i < 6; i++) {
            new Thread(()->{
                try {
                    Thread.sleep(new Random().nextInt(3000));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(Thread.currentThread().getName() + "离开教室");
                countDownLatch.countDown();
            }, "同学" + i).start();
        }

        try {
            // 计数器减到0才会继续执行
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("所有同学离开教室了，可以锁门了");
    }

}
