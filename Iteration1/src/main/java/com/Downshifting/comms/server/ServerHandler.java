package com.Downshifting.comms.server;

import com.Downshifting.common.RPC.ProtoHeader;
import com.Downshifting.common.RPC.RpcProtocol;
import com.Downshifting.common.RPC.RpcRequest;
import com.Downshifting.common.RPC.RpcResponse;
import com.Downshifting.common.constant.MsgType;
import com.Downshifting.common.constant.RpcInvokerType;
import com.Downshifting.invoke.Invoker;
import com.Downshifting.invoke.InvokerFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ServerHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcRequest>> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcProtocol<RpcRequest> rpcProtocol) throws Exception {
        final RpcRequest rpcRequest = rpcProtocol.getBody();
        final RpcResponse response = new RpcResponse();
        final RpcProtocol<RpcResponse> resRpcProtocol = new RpcProtocol();
        final ProtoHeader header = rpcProtocol.getHeader();
        header.setMsgType((byte) MsgType.RESPONSE.ordinal());
        resRpcProtocol.setHeader(header);
        final Invoker invoker = InvokerFactory.get(RpcInvokerType.JDK);
        try {
            final Object data = invoker.invoke(rpcRequest);
            response.setData(data);
        }catch (Exception e){
            response.setException(e);
        }
        resRpcProtocol.setBody(response);
        channelHandlerContext.writeAndFlush(resRpcProtocol);
    }
}
