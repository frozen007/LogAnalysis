package com.changyou.concurrent;

import java.util.LinkedList;
import java.util.List;

public class CommonQueue<T> {

    private int defWaitTime = 5 * 1000;

    //object pool list
    private LinkedList<T> pool = new LinkedList<T>();

    //add object lock
    public final Object putLock = new Object();

    //default max size of the pool, default value is 500.
    private int maxPoolSize = 500;

    /**
     * default constructor, use default wait time
     */
    public CommonQueue() {

    }

    /**
     * constructor, appoint wait time
     * 
     * @param waitTime second
     * @param maxPoolSize
     */
    public CommonQueue(int waitTime, int maxPoolSize) {

        this.defWaitTime = waitTime * 1000;
        this.maxPoolSize = maxPoolSize;

    }

    /**
     * put object to list end 
     * @param data
     */
    public synchronized void put(T data) {

        put(data, true);

    }

    public synchronized void putAll(List<T> datas) {

        int poolSize = pool.size();

        int addSize = datas.size();

        if ((poolSize + addSize) >= maxPoolSize) {
            throw new RuntimeException("pool size overflow, max size is " + maxPoolSize);
        }

        pool.addAll(datas);
        this.notifyAll();

    }

    /**
     * put object to list end 
     * @param data
     */
    public synchronized void put(T data, boolean notifyFlag) {

        int poolSize = pool.size();

        if (poolSize >= maxPoolSize) {
            throw new RuntimeException("pool size overflow, max size is " + maxPoolSize);
        }

        pool.add(data);

        if (notifyFlag)
            this.notifyAll();

    }

    /**
     * insert object to list front
     * @param data
     */
    public synchronized void insert(T data) {

        pool.addFirst(data);

    }

    /**
     * munal active the wait thread.
     *
     */
    public synchronized void activeThread() {

        this.notifyAll();

    }

    /**
     * get object from the front list.
     *  
     * @return
     * @throws Exception
     */
    public synchronized T get() throws Exception {

        while (pool.size() < 1) {

            synchronized (putLock) {
                putLock.notifyAll();
            }

            this.wait(defWaitTime);
        }

        return pool.removeFirst();

    }

    /**
     * get object array from the pool.
     * 
     * @return
     * @throws Exception
     */
    public synchronized List<T> getList(int maxSize) throws Exception {

        while (pool.size() < 1) {

            synchronized (putLock) {
                putLock.notifyAll();
            }

            this.wait();
        }

        LinkedList<T> list = new LinkedList<T>();
        int size = pool.size();

        //get object list 
        size = size < maxSize ? size : maxSize;
        for (int i = 0; i < size; i++) {
            list.addLast(pool.removeFirst());
        }

        return list;

    }

    /**
     * query object pool size.
     * 
     * @return
     */
    public synchronized int size() {

        return pool.size();

    }

    /**
     * Removes all of the elements from this list.
     * 
     */
    public synchronized void clear() {

        pool.clear();

    }

}
