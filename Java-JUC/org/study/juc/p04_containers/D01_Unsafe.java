package org.study.juc.p04_containers;

// NOTE
//  list 返回普通列表
//  set 没有排序需求，有去重需求
//  map 组装复杂数据的对象，发送给前端

// FIXME
//  问题：什么叫线程安全呢？读的数据可能是旧数据？但这好像是可以的。

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * List、Hashset、Hashmap是线程不安全的，因为没加锁
 */
public class D01_Unsafe {

    // 抛出了异常：ConcurrentModificationException:
    private static void unsafeList() {
        ArrayList<Integer> list = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            new Thread(()->{
                list.add(23);
                System.out.println(list);
            }).start();
        }

        // 添加了sleep后，list.size()是100，表示确实插入了100条数据。为什么会报异常呢？
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("list.size() = " + list.size());
    }

    private static void synchronizedList() {
//        ArrayList<Integer> list = new ArrayList<>();
//        List<Integer> synList = Collections.synchronizedList(list);

//        Vector<Integer> vector = new Vector<>();

        CopyOnWriteArrayList<Integer> list = new CopyOnWriteArrayList<>();

        for (int i = 0; i < 100; i++) {
            new Thread(()->{
//                synList.add(23);
//                System.out.println(synList);

//                vector.add(23);
//                System.out.println(vector);

                list.add(45);
                System.out.println(list);
            }).start();
        }

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

//        System.out.println("synList.size() = " + synList.size());
        System.out.println("list.size() = " + list.size());
//        System.out.println("vector.size() = " + vector.size());
    }


    private static void unsafeSet() {
        HashSet<String> hashSet = new HashSet<>();
        Set<String> synchronizededSet = Collections.synchronizedSet(hashSet);

        for (int i = 0; i < 100; i++) {
            new Thread(()->{
                synchronizededSet.add(UUID.randomUUID().toString().substring(0,8));
                System.out.println(synchronizededSet);
            }).start();
        }

    }

    public static void main(String[] args) {
//        synchronizedList();
        unsafeSet();

        /**
         * 只调用一次 unsafeList(); 调用sout可能出现 ConcurrentModificationException 异常
         * 原因：checkForComodification()
         */
//        unsafeList();


        /**
         * 调用多次 unsafeList(); 配合没有sout的情况下， 出现过 ArrayIndexOutOfBoundsException 异常
         */
//        for (int i = 0; i < 50; i++) {
//            unsafeList();
//        }



    }
}
