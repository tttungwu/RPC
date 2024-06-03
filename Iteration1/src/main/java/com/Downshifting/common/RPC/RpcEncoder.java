package com.Downshifting.common.RPC;

import com.Downshifting.common.constants.RpcSerializationType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import com.Downshifting.socket.serialization.RpcSerialization;
import com.Downshifting.socket.serialization.SerializationFactory;


public class RpcEncoder extends MessageToByteEncoder<RpcProtocol<Object>> {

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcProtocol<Object> msg, ByteBuf byteBuf) throws Exception {
        ProtoHeader header = msg.getHeader();
        byteBuf.writeShort(header.getMagic());
        byteBuf.writeByte(header.getVersion());
        byteBuf.writeByte(header.getMsgType());
        byteBuf.writeByte(header.getStatus());
        byteBuf.writeLong(header.getRequestId());
        byteBuf.writeInt(header.getSerializationLen());
        final byte[] ser = header.getSerialization();
        final String serialization = new String(ser);
        byteBuf.writeBytes(ser);
        RpcSerialization rpcSerialization = SerializationFactory.get(RpcSerializationType.get(serialization));
        byte[] data = rpcSerialization.serialize(msg.getBody());
        byteBuf.writeInt(data.length);
        byteBuf.writeBytes(data);
    }
}
