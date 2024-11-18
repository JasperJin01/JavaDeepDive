package org.study.jvm.heap;

public class D01_HeapSpace {
    public static void main(String[] args) {

        //返回Java虚拟机中的堆内存总量
        long initialMemory = Runtime.getRuntime().totalMemory() / 1024;
        //返回Java虚拟机试图使用的最大堆内存量
        long maxMemory = Runtime.getRuntime().maxMemory() / 1024;

        //起始内存
        System.out.println("-Xms : " + initialMemory + "K，" + initialMemory / 1024 + "M");
        //最大内存
        System.out.println("-Xmx : " + maxMemory + "K，" + maxMemory / 1024 + "M");
    }

}
