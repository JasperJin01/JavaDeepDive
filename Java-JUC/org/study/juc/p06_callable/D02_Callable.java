package org.study.juc.p06_callable;


import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

class MyCallable2 implements Callable<Integer> {
    @Override
    public Integer call() throws Exception {
        for (int i = 0; i < 10; i++) {
            Thread.sleep(300);
            System.out.println(Thread.currentThread().getName() + "callable" + i);
        }
        return new Random().nextInt(100);
    }
}
public class D02_Callable {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        FutureTask<Integer> futureTask = new FutureTask<>(new MyCallable2());
        new Thread(futureTask, "线程A").start();

        // 任务状态查询 futureTask.isDone(): 任务是否执行完毕
        System.out.println("isDone 1: " + futureTask.isDone());
        System.out.println("isCancelled 1: " + futureTask.isCancelled());

        Thread.sleep(1000);
//        futureTask.cancel(true); // 子线程会立即打断并抛出异常，不返回结果
        futureTask.cancel(false); // 子线程会「继续执行」并抛出异常，不返回结果


        Integer res = futureTask.get();

        System.out.println("isDone 2: " + futureTask.isDone());

        System.out.println("res = " + res);



    }

}
