package com.Downshifting.common.RPC;

import com.Downshifting.common.constants.RpcSerializationType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import com.Downshifting.common.constants.MsgType;
import com.Downshifting.common.constants.ProtocolConstants;
import com.Downshifting.socket.serialization.RpcSerialization;
import com.Downshifting.socket.serialization.SerializationFactory;


import java.util.*;


public class RpcDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < ProtocolConstants.HEADER_TOTAL_LEN) {
            return;
        }
        in.markReaderIndex();
        short magic = in.readShort();
        if (magic != ProtocolConstants.MAGIC) {
            throw new IllegalArgumentException("magic number is illegal, " + magic);
        }
        byte version = in.readByte();
        byte msgType = in.readByte();
        byte status = in.readByte();
        long requestId = in.readLong();
        final int len = in.readInt();
        if (in.readableBytes() < len){
            in.resetReaderIndex();
            return;
        }
        final byte[] bytes = new byte[len];
        in.readBytes(bytes);
        final String serialization = new String(bytes);
        int dataLength = in.readInt();
        if (in.readableBytes() < dataLength) {
            // 回退标记位置
            in.resetReaderIndex();
            return;
        }
        byte[] data = new byte[dataLength];
        in.readBytes(data);

        MsgType msgTypeEnum = MsgType.fromOrdinal(msgType);
        if (msgTypeEnum == null) {
            return;
        }

        // 构建消息头
        ProtoHeader header = new ProtoHeader();
        header.setMagic(magic);
        header.setVersion(version);
        header.setStatus(status);
        header.setRequestId(requestId);
        header.setMsgType(msgType);
        header.setSerialization(bytes);
        header.setSerializationLen(len);
        header.setMsgLen(dataLength);
        RpcSerialization rpcSerialization = SerializationFactory.get(RpcSerializationType.get(serialization));
        RpcProtocol protocol = new RpcProtocol();
        protocol.setHeader(header);
        switch (msgTypeEnum) {
            // 请求消息
            case REQUEST:
                RpcRequest request = rpcSerialization.deserialize(data, RpcRequest.class);
                protocol.setBody(request);
                break;
            // 响应消息
            case RESPONSE:
                RpcResponse response = rpcSerialization.deserialize(data, RpcResponse.class);
                protocol.setBody(response);
                break;
        }
        out.add(protocol);
    }


}
