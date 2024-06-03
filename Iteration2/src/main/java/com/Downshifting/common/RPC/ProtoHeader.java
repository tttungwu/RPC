package com.Downshifting.common.RPC;


import java.io.Serializable;


public class ProtoHeader implements Serializable {
    // 魔数
    private Short magic;
    // 协议版本号
    private Byte version;
    // 请求类型
    private Byte msgType;
    // 请求状态
    private Byte status;
    // 请求ID
    private Long requestId;
    // 序列化类型
    private Byte serializationType;
    // 请求体长度
    private Integer msgLen;

    public Short getMagic() {
        return magic;
    }

    public void setMagic(Short magic) {
        this.magic = magic;
    }

    public Byte getVersion() {
        return version;
    }

    public void setVersion(Byte version) {
        this.version = version;
    }

    public Byte getMsgType() {
        return msgType;
    }

    public void setMsgType(Byte msgType) {
        this.msgType = msgType;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public Byte getSerializationType() {
        return serializationType;
    }

    public void setSerializationType(Byte serializationType) {
        this.serializationType = serializationType;
    }

    public Integer getMsgLen() {
        return msgLen;
    }

    public void setMsgLen(Integer msgLen) {
        this.msgLen = msgLen;
    }
}
