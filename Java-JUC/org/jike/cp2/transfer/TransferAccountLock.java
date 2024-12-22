package org.jike.cp2.transfer;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TransferAccountLock {
}

/**
 * 死锁的四个条件：
 * 互斥、请求与保持、不可抢占、循环等待
 * 打破死锁：
 * 打破请求和保持：利用统一的管理员，统一分配所有的锁
 * 不可抢占：使用JUC中的Lock
 */
class Account {
    private int balance;
    private final Lock lock = new ReentrantLock();
    public Account() {}
    public Account(int balance) { this.balance = balance;}
    public int getBalance() { return balance; }

    // 死锁示例：如果两个账户分别向对方转账，就可能导致死锁
    void transferDeadLock(Account tar, int amt) {
        this.lock.lock();
        try {

            // 为了测试死锁加的
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            tar.lock.lock();
            try {
                // 转账
                this.balance -= amt;
                tar.balance += amt;
            } finally {
                tar.lock.unlock();
            }
        } finally {
            this.lock.unlock();
        }
    }

    // 转账，使用tryLock()，但可能导致活锁
    void transferTryLockLiveLock(Account tar, int amt) {
        // 可能存在活锁问题，比如账户A和B同时给对方转账
        // A获取了accountA的锁，B获取了accountB的锁，他们到tar.lock的时候会失败
        // 然后可能会同时释放掉this.lock，又同时获取自己的锁，然后尝试获取对方的锁，一直循环
        while (true) {
            if (this.lock.tryLock()) {
                try {
                    if (tar.lock.tryLock()) {
                        try {
                            this.balance -= amt;
                            tar.balance += amt;
                        } finally {
                            tar.lock.unlock();
                        }
                    }
                } finally {
                    this.lock.unlock();
                }
            }
        }
    }

    // (正确) 通过等待随机秒避免活锁
    void transferTryLockRandomWait(Account tar, int amt) {
        final int MAX_RETRIES = 5; // 最大重试次数
        int retries = 0;
        while (retries < MAX_RETRIES) {
            if (this.lock.tryLock()) {
                try {
                    if (tar.lock.tryLock()) {
                        try {
                            this.balance -= amt;
                            tar.balance += amt;
                            return; // 转账成功，退出方法
                        } finally {
                            tar.lock.unlock();
                        }
                    }
                } finally {
                    this.lock.unlock();
                }
            }

            retries++;
            System.out.println("重试次数: " + retries);
            try {
                // 等待一段随机的时间，避免所有线程同时重试
                Thread.sleep((long) (Math.random() * 100)); // 随机等待
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("转账失败，超过最大重试次数");
    }

    // System.identityHashCode 和 Object.hashCode 的区别？
    //

    // (正确) 使用lock，使用hash固定加锁顺序，打破了
    void transferOrder(Account tar, int amt) {
        // 保证总是按照账户的顺序获取锁
        Account first = this, second = tar;
        if (System.identityHashCode(this) > System.identityHashCode(tar)) {
            first = tar;
            second = this;
        } // 在idea中，idea会为重新分配过地址的变量加上下划线

        // 按照顺序获取锁
        first.lock.lock();
        try {
            second.lock.lock();
            try {
                this.balance -= amt;
                tar.balance += amt;
            } finally {
                second.lock.unlock();
            }
        } finally {
            first.lock.unlock();
        }
    }


    public static void main(String[] args) {
        Account accountA = new Account(500);
        Account accountB = new Account(1000);

        // 测试 1：死锁 - transferDeadLock
        Thread t1 = new Thread(() -> {
//            accountA.transferDeadLock(accountB, 100);
            accountA.transferTryLockRandomWait(accountB, 100);
            System.out.println("转账后，账户A余额: " + accountA.getBalance() + ", 账户B余额: " + accountB.getBalance());
        });
        Thread t2 = new Thread(() -> {
//            accountB.transferDeadLock(accountA, 50);
            accountB.transferTryLockRandomWait(accountA, 50);
            System.out.println("转账后，账户A余额: " + accountA.getBalance() + ", 账户B余额: " + accountB.getBalance());
        });

        t1.start();
        t2.start();


    }
}
