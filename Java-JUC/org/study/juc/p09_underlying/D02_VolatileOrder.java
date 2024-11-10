package org.study.juc.p09_underlying;

public class D02_VolatileOrder {
    static int a, b;
    static int x, y;

    // 有序性
    public static void main(String[] args) throws InterruptedException {
        int i = 0;
        while (true) {
            i++;
            a = b = x = y = 0;

            Thread thread1 = new Thread(() -> {
                a = 1;
                x = b;
            });

            Thread thread2 = new Thread(() -> {
                b = 1;
                y = a;
            });

            thread1.start();
            thread2.start();

            thread1.join();
            thread2.join();

            System.out.println("第 " + i + " 次打印，x = " + x +" , y = " + y);

            if (x == 0 && y == 0) break;
        }
    }
}
