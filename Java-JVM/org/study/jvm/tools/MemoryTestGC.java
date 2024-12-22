package org.study.jvm.tools;

import java.util.List;
import java.util.ArrayList;

public class MemoryTestGC {
    static class Data {
        private byte[] memory;

        public Data(int sizeInMB) {
            this.memory = new byte[sizeInMB * 1024 * 1024]; // 每个对象占用指定大小的内存
        }
    }
    public static void main(String[] args) {
        List<Data> list = new ArrayList<>();
        int count = 0;

        try {
            // 模拟内存分配，不断创建新对象
            while (true) {
                list.add(new Data(1)); // 每个对象占用 1MB 内存
                new Data(1);
                count++;
                if (count % 10 == 0) { // 每创建 10 个对象，打印状态并暂停一段时间
                    System.out.println("Created " + count + " MB of objects.");
                    Thread.sleep(500); // 暂停 0.5 秒，避免过快耗尽内存
                }
            }
        } catch (OutOfMemoryError e) {
            // 捕获 OutOfMemoryError 并释放内存
            System.out.println("Out of memory! Clearing list...");
            list.clear(); // 清空列表，释放堆内存
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
