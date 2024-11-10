package org.study.os.memory;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.Math.pow;

public class Process {
    public int ID;
    public int requestSize;
    public Color color = new Color((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255), 100);

    public static int allID;

    public int size;
    public int[] address = {0, 0};

    Process(){}
    Process(int ID,int rSize){
        this.ID = ID;
        this.requestSize = rSize;
        //System.out.println("产生了所需内存为" + requestSize + "的进程，编号为" + ID);
        this.size = (int) pow(2, (int)Math.ceil(Math.log(rSize)/Math.log(2)));
    }

    public void setAddress(int[] address) {
        this.address = address;
    }

    public int[] getAddress() {
        return this.address;
    }

    public static void init() {
        allID = 0;
    }
    public static Process createProcess(int pageSize, int maxGroupNumber, int rSize) { return new Process(++allID, rSize); }
    public static Process createProcess(int pageSize, int maxGroupNumber) {
        int mxS = pageSize * (1 << (maxGroupNumber - 1));
        //System.out.println(pageSize + " " + maxGroupNumber);
        return createProcess(pageSize, maxGroupNumber, (new Random().nextInt(mxS - 1) + 1) / pageSize * pageSize);
    }
}
//8

