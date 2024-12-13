package org.study.jvm.test;

public class MainTest1 {
    public static void main(String[] args) {
        System.out.println("Hello World!");
    }
}


/**
 * fixme 这段代码并没有像讲义中说的那样出现很多sout报错的问题啊？
 */
class ThreadSafeSample {
    public int sharedState;
    public void nonSafeAction() {
        while (sharedState < 10000000) {
            int former = sharedState++;
            int latter = sharedState;
            if (former != latter - 1) {
                System.out.printf("Observed data race, former is " +
                        former + ", " + "latter is " + latter);
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ThreadSafeSample sample = new ThreadSafeSample();
        Thread threadA = new Thread(){
            public void run(){
                sample.nonSafeAction();
            }
        };
        Thread threadB = new Thread(){
            public void run(){
                sample.nonSafeAction();
            }
        };
        System.out.println("start");
        threadA.start();
        threadB.start();
        threadA.join();
        threadB.join();
    }
}