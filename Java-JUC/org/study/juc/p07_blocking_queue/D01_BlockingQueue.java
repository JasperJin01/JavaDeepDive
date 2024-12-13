package org.study.juc.p07_blocking_queue;

import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingDeque;

/**
 * <b>阻塞队列：</b>
 * 和普通的队列相比，阻塞队列是有长度的
 * 当阻塞队列已满，并且继续入队时，入队操作的线程会被阻塞（等待队列有空位置才能继续入队操作）
 * 当阻塞队列为空，并且继续出队时，出队操作的线程会被阻塞（等待队列有数据才能出队）
 */
public class D01_BlockingQueue {

    ArrayBlockingQueue<String> arrayBlockingQueue = new ArrayBlockingQueue(3);
    // TODO ArrayBlockingQueue 有一系列的入队、出队方法
    @Test
    public void test01() {
        System.out.println(arrayBlockingQueue.add("a"));
        System.out.println(arrayBlockingQueue.add("b"));
        System.out.println(arrayBlockingQueue.add("c"));
//        System.out.println(arrayBlockingQueue.add("d")); // 队列已满仍然入队，报异常
    }
    @Test
    public void test02() {
        System.out.println(arrayBlockingQueue.add("a"));
        System.out.println(arrayBlockingQueue.add("b"));
        System.out.println(arrayBlockingQueue.add("c"));

    }



}

/*
 * NOTE idea 的一些快捷键
 *  cmd + opt + t: Code->surround with
 *  ctrl + h: Navigate->Hierarchy
 */
