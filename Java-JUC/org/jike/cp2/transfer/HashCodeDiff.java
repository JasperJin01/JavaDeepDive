package org.jike.cp2.transfer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * System.identityHashCode 和 Object.hashCode 的区别
 */
public class HashCodeDiff {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class User {
        int id;
        String name;
    }

    static void diffTest1() {
        String str1 = new String("abc");
        String str2 = new String("abc");

        // str1和str2的hashCode是相同的，是因为String类重写了hashCode方法，它根据String的值来确定hashCode的值
        System.out.println("str1 hashCode: " + str1.hashCode()); // 96354
        System.out.println("str2 hashCode: " + str2.hashCode()); // 96354

        // 不一样，identityHashCode 根据对象物理内存地址产生hash值
        // 每个String对象的物理地址不一样，identityHashCode也会不一样
        System.out.println("str1 identityHashCode: " + System.identityHashCode(str1));
        System.out.println("str2 identityHashCode: " + System.identityHashCode(str2));

        User user = new User(1,"test");
        System.out.println("user.hashCode() = " + user.hashCode());
        System.out.println("user hashCode: " + user.hashCode());
        System.out.println("user identityHashCode: " + System.identityHashCode(user));
    }

    public static void main(String[] args) {
        diffTest1();
    }


}
