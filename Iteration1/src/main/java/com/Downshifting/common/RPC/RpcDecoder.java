package com.Downshifting.common.RPC;

import com.Downshifting.common.constants.RpcSerializationType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import com.Downshifting.common.constants.MsgType;
import com.Downshifting.common.constants.ProtocolConstants;
import com.Downshifting.comms.serialization.RpcSerialization;
import com.Downshifting.comms.serialization.SerializationFactory;


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
        final byte serializationType = in.readByte();
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
        header.setSerializationType(serializationType);;
        header.setMsgLen(dataLength);
        RpcSerialization rpcSerialization = SerializationFactory.get(RpcSerializationType.fromOrdinal(serializationType));
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
