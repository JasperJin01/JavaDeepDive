package org.study.os.memory;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Buddy {
    //均以kb为单位
    public int maxGroupNumber = 10;//块组
    public int maxBlockSize = 0;//块最大大小
    public int pageSize = 0;//页框大小
    public int memory = 0;//内存大小
    public int freeMemorySize = 0;//自由内存大小
    public char[] memoryAddress; // TODO 这个东西用过吗
    public Map<Integer, List> freeBlock = new HashMap<>();//空闲块组链表
    //public Map<Integer, List> takenBlock = new HashMap<>();//占用块组链表
    public ShowPanel ui;

    Buddy() {
    }

    Buddy(int pageSize, int memory) {
        this.pageSize = pageSize;
        this.memory = memory;
        this.maxBlockSize = (1 << (maxGroupNumber - 1)) * pageSize;
        this.freeMemorySize = 3 * maxBlockSize;
        memoryAddress = new char[memory];
    }

    public void init() {
        for (int i = 0; i < maxGroupNumber; i++) {
            List<int[]> list = new ArrayList<>();
            freeBlock.put((1 << i) * pageSize, list);
        }
        for (int i = 0; i < memoryAddress.length; i++)
            memoryAddress[i] = '_';
        // System.out.println("sum is " + freeMemorySize/((1 << (maxGroupNumber - 1)) * pageSize));
        for (int i = 0; i < freeMemorySize / ((1 << (maxGroupNumber - 1)) * pageSize); i++)
            freeBlock.get((1 << (maxGroupNumber - 1)) * pageSize).add(new int[]{i * ((1 << (maxGroupNumber - 1)) * pageSize), (i + 1) * ((1 << (maxGroupNumber - 1)) * pageSize) - 1});
    }

    public void showBlockChain() {
        System.out.println("--- show block chain ---");
        for (int i = 0; i < maxGroupNumber; i++) {
            List<int[]> temp = freeBlock.get((1 << i) * pageSize);
            // System.out.println("大小为" + (1 << i) * pageSize + "的地址:");
            for (int j = 0; j < temp.size(); j++)
                System.out.println("第" + (j + 1) + "块为" + temp.get(j)[0] + "to" + temp.get(j)[1]);
        }
    }

    public boolean request(Process process) {
        if (process.size > maxBlockSize * pageSize) {
            System.out.println("无法分配，因为process.size过大，process.size > maxBlockSize * pageSize");
            return false;
        }
        List<int[]> temp = freeBlock.get(process.size);
        boolean flag = false;
        if (temp.size() == 0) {
            flag = splitBlock(process.size * 2, process);
            if (!flag) {
                System.out.println("无法分配，因为 splitBlock 失败");
                return false;
            }
        }
        useBlock(process.size, process);
        return true;
    }

    public boolean splitBlock(int size, Process process) {
        boolean flag = false;
        List<int[]> temp = freeBlock.get(size);
        if (temp == null) return false;
        if (temp.size() == 0) {
            flag = splitBlock(size * 2, process);
        } else flag = true;
        if (flag) {
            int[] addr = temp.get(0);
            temp.remove(0);
            freeBlock.put(size, temp);

            int[] leftSon = new int[2];
            int[] rightSon = new int[2];
            leftSon[0] = addr[0];
            leftSon[1] = addr[0] + size / 2 - 1;
            rightSon[0] = addr[0] + size / 2;
            rightSon[1] = addr[1];
            List<int[]> temp1 = freeBlock.get(size / 2);
            temp1.add(leftSon);
            temp1.add(rightSon);
            freeBlock.put(size / 2, temp1);
            return true;
        } else return false;
    }

    public boolean useBlock(int size, Process process) {
        List<int[]> temp = freeBlock.get(size);
        int[] addr = temp.get(0);
        temp.remove(0);
        freeBlock.put(size, temp);
        process.setAddress(addr);
        //System.out.println("所需内存为" + process.requestSize + "的进程" + process.ID + "占用了" + addr[0] + "到" + addr[1] + "的一段连续内存");
        return true;
    }

    public boolean releaseBlock(Process process) {
        int[] addr = process.getAddress();
        int l = addr[0];
        int r = addr[1];
        int size = r - l + 1;
        int[] block = combineBlock(l, r);
        size = block[1] - block[0] + 1;
        List<int[]> temp = freeBlock.get(size);
        temp.add(block);
        freeBlock.put(size, temp);
        return true;
    }

    public int[] combineBlock(int l, int r) {
        boolean flag = true;
        int size = r - l + 1;
        while (flag) {
            flag = false;
            List<int[]> temp = freeBlock.get(size);
            if (size == maxBlockSize) break;
            for (int i = 0; i < temp.size(); i++) {
                int[] block = temp.get(i);
                if (block[1] == l - 1) {
                    l = block[0];
                    temp.remove(i);
                    freeBlock.put(size, temp);
                    size *= 2;
                    flag = true;
                    break;
                } else if (block[0] == r + 1) {
                    r = block[1];
                    temp.remove(i);
                    freeBlock.put(size, temp);
                    size *= 2;
                    flag = true;
                    break;
                }
            }
        }
        return new int[]{l, r};
    }

}