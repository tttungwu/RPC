package cn.edu.xmu.comms.server;

import cn.edu.xmu.common.RPC.ProtoHeader;
import cn.edu.xmu.common.RPC.RpcProtocol;
import cn.edu.xmu.common.RPC.RpcRequest;
import cn.edu.xmu.common.RPC.RpcResponse;
import cn.edu.xmu.common.constants.MsgType;
import cn.edu.xmu.common.constants.RpcInvokerType;
import cn.edu.xmu.common.utils.ServerCache;
import cn.edu.xmu.filter.FilterData;
import cn.edu.xmu.filter.FilterResponse;
import cn.edu.xmu.invoke.Invoker;
import cn.edu.xmu.invoke.InvokerFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ServerHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcRequest>> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcProtocol<RpcRequest> rpcProtocol) throws Exception {
        FilterResponse filterResponse = ServerCache.beforeFilterChain.doFilter(new FilterData<>(rpcProtocol));
        final RpcRequest rpcRequest = rpcProtocol.getBody();
        final RpcResponse response = new RpcResponse();
        final RpcProtocol<RpcResponse> resRpcProtocol = new RpcProtocol();
        final ProtoHeader header = rpcProtocol.getHeader();
        header.setMsgType((byte) MsgType.RESPONSE.ordinal());
        resRpcProtocol.setHeader(header);

        if (filterResponse.getIsAccepted()) {
            final Invoker invoker = InvokerFactory.get(RpcInvokerType.JDK);
            try {
                final Object data = invoker.invoke(rpcRequest);
                response.setData(data);
            }catch (Exception e){
                response.setException(e);
            }
        } else {
            response.setException(filterResponse.getException());
        }

        resRpcProtocol.setBody(response);
        channelHandlerContext.writeAndFlush(resRpcProtocol);
    }
}