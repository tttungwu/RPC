package com.Downshifting.common.RPC;

import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;


public class RpcFuture <T> {
    // 异步结果
    private Promise<T> promise;
    // 异步操作的超时时间
    private Long timeout;

    public RpcFuture() {
    }

    public RpcFuture(Promise<T> promise, Long timeout) {
        this.promise = promise;
        this.timeout = timeout;
    }

    public Promise<T> getPromise() {
        return promise;
    }

    public void setPromise(Promise<T> promise) {
        this.promise = promise;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }
}
