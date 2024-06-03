package com.Downshifting.common.RPC;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class RpcRequestTracker {
    // 请求ID
    public final static AtomicLong REQUEST_ID_GEN = new AtomicLong(0);
    // 绑定请求
    public static final ConcurrentMap<Long, RpcFuture<RpcResponse>> REQUEST_MAP = new ConcurrentHashMap<>();

    // 生成请求ID
    public static Long getRequestId(){
        if (REQUEST_ID_GEN.longValue() == Long.MAX_VALUE){
            REQUEST_ID_GEN.set(0);
        }
        return REQUEST_ID_GEN.incrementAndGet();
    }
}
