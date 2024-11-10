package org.study.juc.p07_blocking_queue;

import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class D02_ProducerAndConsumer {
    public static void main(String[] args) {
        ArrayBlockingQueue<Integer> arrayBlockingQueue = new ArrayBlockingQueue<>(3);

        // 理论上没有容器长度的限制，实际上是Integer.MAX_VALUE
        LinkedBlockingQueue<Integer> linkedBlockingQueue = new LinkedBlockingQueue<>();


        // 阻塞： put和take方法
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {

                try {
                    arrayBlockingQueue.put(i + 1);
                    System.out.println("+++ 生产者生产第 " + (i + 1) + " 根棒棒糖，当前队列共有 " + arrayBlockingQueue.size() + " 根棒棒糖");
//                    synchronized (System.out) { // 加锁，确保每条输出的完整性
//                        System.out.println("+++ 生产者生产第 " + (i + 1) + " 根棒棒糖，当前队列共有 " + arrayBlockingQueue.size() + " 根棒棒糖");
//                    }

                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
        }, "生产者").start();

        new Thread(() -> {

            for (int i = 0; i < 10; i++) {

                try {
                    System.out.println("--- 消费者消费第 " + arrayBlockingQueue.take() + " 根棒棒糖，当前队列共有 " + arrayBlockingQueue.size() + " 根棒棒糖");
//                    int item = arrayBlockingQueue.take();
//                    synchronized (System.out) { // 加锁，确保每条输出的完整性
//                        System.out.println("--- 消费者消费第 " + item + " 根棒棒糖，当前队列共有 " + arrayBlockingQueue.size() + " 根棒棒糖");
//                    }

                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }

        }, "消费者").start();


    }



    public void dead() { // 加了 syn 死锁了？？执行不下去
        ArrayBlockingQueue<Integer> arrayBlockingQueue = new ArrayBlockingQueue<>(3);

        // 理论上没有容器长度的限制，实际上是Integer.MAX_VALUE
        LinkedBlockingQueue<Integer> linkedBlockingQueue = new LinkedBlockingQueue<>();


        // 阻塞： put和take方法
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {

//                synchronized (arrayBlockingQueue) {
                    try {
                        arrayBlockingQueue.put(i + 1);
                        System.out.println("+++ 生产者生产第 " + (i + 1) + " 根棒棒糖，当前队列共有 " + arrayBlockingQueue.size() + " 根棒棒糖");
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
//                }

            }
        }, "生产者").start();

        new Thread(() -> {

            for (int i = 0; i < 10; i++) {

//                synchronized (arrayBlockingQueue) {
                    try {
                        System.out.println("--- 消费者消费第 " + arrayBlockingQueue.take() + " 根棒棒糖，当前队列共有 " + arrayBlockingQueue.size() + " 根棒棒糖");
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
//                }

            }

        }, "消费者").start();


    }

}
