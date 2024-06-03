package com.Downshifting.common.RPC;

import java.io.Serializable;


public class RpcResponse implements Serializable {
    // 成功调用返回数据
    private Object data;
    // 调用失败抛出异常
    private Exception exception;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }
}
