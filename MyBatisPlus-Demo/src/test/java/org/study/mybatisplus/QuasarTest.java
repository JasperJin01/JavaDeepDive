package org.study.mybatisplus;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.strands.Strand;
import org.springframework.util.StopWatch;

import java.util.concurrent.CountDownLatch;
import java.util.stream.IntStream;

public class QuasarTest {
    // FIXME 报错！不太清楚原因，可能是版本问题。
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch count = new CountDownLatch(10000);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        IntStream.range(0,10000).forEach(i -> new Fiber() { // co.paralleluniverse.fibers.Fiber
            @Override
            protected String run() throws SuspendExecution, InterruptedException {
                // Quasar 中 Thread 和 Fiber 都被称为 Strand, Fiber 不能调用 Thread.sleep 休眠
                Strand.sleep(1000);
                count.countDown();
                return "aa";
            }
        }.start());
        count.await();
        stopWatch.stop();
        System.out.println("结束了：" + stopWatch.prettyPrint());
    }

}
