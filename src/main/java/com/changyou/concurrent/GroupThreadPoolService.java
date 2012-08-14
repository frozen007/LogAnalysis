package com.changyou.concurrent;

import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;

public abstract class GroupThreadPoolService<E> extends Thread implements DispatchService<E> {
    private static Logger logger = Logger.getLogger(GroupThreadPoolService.class);

    protected Stack<WorkThread> threadPool = new Stack<WorkThread>();
    protected int poolSize = 1;
    protected ConcurrentLinkedQueue<E> queue = new ConcurrentLinkedQueue<E>();
    protected Object queueLock = new Object();
    protected Object poolLock = new Object();
    protected Object workMonitor = new Object();

    public GroupThreadPoolService() {
        initialize();
    }

    public GroupThreadPoolService(int poolSize) {
        this.poolSize = poolSize;
        initialize();
    }

    protected void initialize() {
        for (int i = 0; i < poolSize; i++) {
            WorkThread workThread = new WorkThread();
            workThread.setName(this.getClass().getSimpleName() + "-worker" + i);
            threadPool.push(workThread);
            workThread.start();
        }
    }

    public void dispatch(E e) {
        queue.offer(e);
        synchronized (queueLock) {
            queueLock.notifyAll();

        }
    }

    @Override
    public void run() {
        while (true) {
            E task = null;
            WorkThread worker = null;
            synchronized (queueLock) {
                while (true) {
                    task = queue.poll();
                    if (task == null) {
                        try {
                            logger.debug("waiting for queue...");
                            queueLock.wait();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("got task:" + task);
                        break;
                    }

                }
            }

            synchronized (poolLock) {
                while (true) {
                    try {
                        if (threadPool.isEmpty()) {
                            logger.debug("waiting for pool...");
                            poolLock.wait(100);
                        } else {
                            worker = threadPool.pop();
                            System.out.println("got worker:" + worker);
                            worker.setTask(task);
                            worker.wakeup();
                            break;
                        }
                    } catch (Exception e) {
                    }
                }
            }

        }

    }

    protected abstract void processSingle(E e);

    public void waitForFinish() {
        while (true) {

            synchronized (queueLock) {
                synchronized (poolLock) {
                    // TODO
                    int currentPoolSize = threadPool.size();
                    System.out.println("queue:" + queue.size() + ", currentPoolSize=" + currentPoolSize);
                    if (queue.isEmpty() && currentPoolSize == poolSize) {
                        break;
                    }
                }
            }

            synchronized (workMonitor) {
                try {
                    workMonitor.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class WorkThread extends Thread {
        protected E e;
        protected Object lock = new Object();

        public void run() {

            while (true) {
                synchronized (lock) {
                    try {

                        System.out.println(this.getName() + " waiting...");
                        lock.wait();

                        System.out.println("working for :" + e);
                        processSingle(e);

                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    returnToPool(this);
                }
            }
        }

        public void setTask(E e) {
            this.e = e;
        }

        public void wakeup() {
            synchronized (lock) {
                lock.notify();
            }
        }
    }

    private void returnToPool(WorkThread workThread) {
        synchronized (poolLock) {
            threadPool.push(workThread);
            poolLock.notifyAll();
        }
        System.out.println("return to pool for " + workThread.e);
    }
}
