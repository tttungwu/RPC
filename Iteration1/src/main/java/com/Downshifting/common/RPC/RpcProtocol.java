package com.Downshifting.common.RPC;

import com.Downshifting.common.RPC.ProtoHeader;

import java.io.Serializable;

public class RpcProtocol<T> implements Serializable {
    // 请求头
    private ProtoHeader header;
    // 请求体
    private T body;

    public ProtoHeader getHeader() {
        return header;
    }

    public void setHeader(ProtoHeader header) {
        this.header = header;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }
}
