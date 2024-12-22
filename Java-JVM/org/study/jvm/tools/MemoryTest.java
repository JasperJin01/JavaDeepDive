package org.study.jvm.tools;

import java.util.ArrayList;
import java.util.List;

public class MemoryTest {
    static class Data {
        private byte[] memory = new byte[1024 * 1024]; // 每个对象占用 1MB
    }

    public static void main(String[] args) {
        List<Data> list = new ArrayList<>();
        int count = 0;
        try {
            while (true) {
                list.add(new Data());
                new Data();
                count++;
                if (count % 10 == 0) {
                    System.out.println("Created " + count + " objects");
                    Thread.sleep(1000); // 每创建 10 个对象，暂停 1 秒
                }
            }
        } catch (OutOfMemoryError e) {
            System.out.println("Out of Memory!");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
