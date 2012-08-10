package com.changyou.concurrent;

import java.util.Collection;
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;

public abstract class GroupThreadPoolService<E> extends Thread implements DispatchService<E> {
    private static Logger logger = Logger.getLogger(GroupThreadPoolService.class);

    protected Stack<WorkThread> threadPool = new Stack<WorkThread>();
    protected int poolSize = 1;
    protected ConcurrentLinkedQueue<E> queue = new ConcurrentLinkedQueue<E>();
    protected Object putLock = new Object();
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
    }

    @Override
    public void run() {
        //TODO:not perfect!!!
        process(queue);
        queue.clear();
    }

    public void process(Collection<E> listE) {
        for (E task : listE) {
            try {
                WorkThread workThread = getWorkThread();
                workThread.setTask(task);
                workThread.wakeup();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    protected abstract void processSingle(E e);

    public void waitForFinish() {
        while (true) {
            synchronized (workMonitor) {
                //TODO
                int queueSize = queue.size();
                int currentPoolSize = threadPool.size();
                if (queueSize == 0 && currentPoolSize == poolSize) {
                    break;
                }

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

        public synchronized void run() {
            while (true) {
                try {
                    wait();

                    processSingle(e);

                } catch (Throwable e) {
                    e.printStackTrace();
                }
                returnToPool(this);
            }
        }

        public void setTask(E e) {
            this.e = e;
        }

        public synchronized void wakeup() {
            notify();
        }
    }

    private WorkThread getWorkThread() throws InterruptedException {
        WorkThread worker = null;
        synchronized (poolLock) {
            while (threadPool.isEmpty()) {
                poolLock.wait();
            }
            worker = threadPool.pop();
        }
        return worker;
    }

    private void returnToPool(WorkThread workThread) {
        synchronized (poolLock) {
            threadPool.push(workThread);
            poolLock.notifyAll();
        }
        synchronized (workMonitor) {
            workMonitor.notifyAll();
        }
    }
}
