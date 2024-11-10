package org.study.juc.p07_blocking_queue;

import java.util.concurrent.SynchronousQueue;

public class D03_SynchronousQueue {
    public static void main(String[] args) throws InterruptedException {
        SynchronousQueue<Integer> queue = new SynchronousQueue<>();
        queue.put(3);
    }
}
