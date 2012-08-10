package com.changyou.concurrent;

import java.util.Collection;
import java.util.List;

/**
 * CommonDispatchService
 * A single thread service with a FIFO queue to consume type E concurrently
 * 
 * @author frozen007@sohu.com
 */
public abstract class CommonDispatchService<E> extends Thread
        implements
            DispatchService<E> {

    protected CommonQueue<E> queue = new CommonQueue<E>();

    public CommonDispatchService() {
        this.setName(this.getClass().getSimpleName());
    }

    public void run() {
        while (true) {
            try {
                List<E> listE = queue.getList(500);
                process(listE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void dispatch(E e) {
        if (e != null) {
            queue.put(e);
        }
    }

    public abstract void process(Collection<E> listE);
}
