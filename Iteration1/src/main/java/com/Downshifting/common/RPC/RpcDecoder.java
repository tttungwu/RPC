package com.Downshifting.common.RPC;

import com.Downshifting.common.constant.MsgType;
import com.Downshifting.common.constant.ProtocolConstants;
import com.Downshifting.common.constant.RpcSerializationType;
import com.Downshifting.comms.serialization.RpcSerialization;
import com.Downshifting.comms.serialization.SerializationFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class RpcDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        byteBuf.markReaderIndex();
        short magic = byteBuf.readShort();
        if (magic != ProtocolConstants.MAGIC) {
            throw new IllegalArgumentException("magic number is illegal: " + magic);
        }
        byte version = byteBuf.readByte();
        byte msgType = byteBuf.readByte();
        byte status = byteBuf.readByte();
        long requestId = byteBuf.readLong();
        byte serializationType = byteBuf.readByte();
        int dataLength = byteBuf.readInt();
        if (byteBuf.readableBytes() < dataLength) {
            byteBuf.resetReaderIndex();
            return;
        }
        byte[] data = new byte[dataLength];
        byteBuf.readBytes(data);
        MsgType msgTypeEnum = MsgType.fromOrdinal(msgType);

        ProtoHeader header = new ProtoHeader();
        header.setMagic(magic);
        header.setVersion(version);
        header.setStatus(status);
        header.setRequestId(requestId);
        header.setMsgType(msgType);
        header.setSerializationType(serializationType);
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
        list.add(protocol);
    }
}
