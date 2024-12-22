package org.jike.cp2.semaphore;


import java.util.concurrent.Semaphore;

// https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Semaphore.html
public class SemaphoreExample {


}

class Pool {
    private static final int MAX_AVAILABLE = 100;
    private final Semaphore available = new Semaphore(MAX_AVAILABLE, true);

    public Object getItem() throws InterruptedException {
        available.acquire();
        return getNextAvailableItem();
    }

    public void putItem(Object x) {
        if (markAsUnused(x))
            available.release();
    }

    // Not a particularly efficient data structure; just for demo

    protected Object[] items = new DataResource[MAX_AVAILABLE];
    // 在构造器中初始化数组元素
    public Pool() {
        for (int i = 0; i < MAX_AVAILABLE; i++) {
            items[i] = new DataResource(); // 显式初始化每个元素
        }
    }

    protected boolean[] used = new boolean[MAX_AVAILABLE];

    protected synchronized Object getNextAvailableItem() {
        for (int i = 0; i < MAX_AVAILABLE; ++i) {
            if (!used[i]) {
                used[i] = true;
                return items[i];
            }
        }
        return null; // not reached
    }

    protected synchronized boolean markAsUnused(Object item) {
        for (int i = 0; i < MAX_AVAILABLE; ++i) {
            if (item == items[i]) {
                if (used[i]) {
                    used[i] = false;
                    return true;
                } else
                    return false;
            }
        }
        return false;
    }

    static class DataResource {

    }
}