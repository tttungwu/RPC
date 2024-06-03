package com.Downshifting.Register.event;

public interface RpcLister<T> {
    void exec(T t);
}
