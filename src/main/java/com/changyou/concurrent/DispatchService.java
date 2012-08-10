package com.changyou.concurrent;

public interface DispatchService<E> {

    public void dispatch(E e);

}
