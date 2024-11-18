package org.study.jvm.heap;

import java.util.ArrayList;

public class D02_OOM {
    /**
     * -Xms30m -Xmx30m
     */
    public static void main(String[] args) throws InterruptedException {
        ArrayList<byte[]> list = new ArrayList<>();
        while(true){

            System.out.print("最大堆大小：Xmx=");
            System.out.println(Runtime.getRuntime().maxMemory() / 1024.0 / 1024 + "M");

            System.out.print("剩余堆大小：free mem=");
            System.out.println(Runtime.getRuntime().freeMemory() / 1024.0 / 1024 + "M");

            System.out.print("当前堆大小：total mem=");
            System.out.println(Runtime.getRuntime().totalMemory() / 1024.0 / 1024 + "M");

            list.add(new byte[1024*1024]);

            Thread.sleep(100);
        }
    }
    // OOM: Java heap space

}
