package com.Downshifting.common.RPC;

import com.Downshifting.common.constant.RpcSerializationType;
import com.Downshifting.comms.serialization.RpcSerialization;
import com.Downshifting.comms.serialization.SerializationFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class RpcEncoder extends MessageToByteEncoder<RpcProtocol<Object>> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RpcProtocol<Object> objectRpcProtocol, ByteBuf byteBuf) throws Exception {
        ProtoHeader header = objectRpcProtocol.getHeader();
        byteBuf.writeShort(header.getMagic());
        byteBuf.writeByte(header.getVersion());
        byteBuf.writeByte(header.getMsgType());
        byteBuf.writeByte(header.getStatus());
        byteBuf.writeLong(header.getRequestId());
        byteBuf.writeInt(header.getSerializationType());
        RpcSerialization rpcSerialization = SerializationFactory.get(RpcSerializationType.fromOrdinal(header.getSerializationType()));
        byte[] data = rpcSerialization.serialize(objectRpcProtocol.getBody());
        byteBuf.writeInt(data.length);
        byteBuf.writeBytes(data);
    }
}
