package com.Downshifting.comms.client;

import com.Downshifting.common.RPC.*;
import com.Downshifting.common.constant.MsgStatus;
import com.Downshifting.common.constant.MsgType;
import com.Downshifting.common.constant.ProtocolConstants;
import com.Downshifting.common.constant.RpcSerializationType;
import com.Downshifting.common.utils.Endpoint;
import com.Downshifting.service.CalcService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultPromise;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

public class Client {

    private final Bootstrap bootstrap;

    private final EventLoopGroup eventLoopGroup;

    private ChannelFuture channelFuture;

    private Endpoint server;

    public Client(Endpoint server) throws InterruptedException {
        this.server = server;

        bootstrap = new Bootstrap();
        eventLoopGroup = new NioEventLoopGroup(4);
        bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                .addLast(new RpcEncoder())
                                .addLast(new RpcDecoder())
                                .addLast(new ClientHandler());
                    }
                });
        channelFuture = bootstrap.connect(server.getIp(), server.getPort()).sync();
    }

    public void sendRequest(Object request) {
        channelFuture.channel().writeAndFlush(request);
    }

    public static void main(String[] args) throws Exception {
        Client nettyClient = new Client(new Endpoint("127.0.0.1", 8084));
        final RpcProtocol rpcProtocol = new RpcProtocol();
        // 构建消息头
        ProtoHeader header = new ProtoHeader();
        Long requestId = RpcRequestTracker.getRequestId();
        header.setMagic(ProtocolConstants.MAGIC);
        header.setVersion(ProtocolConstants.VERSION);
        header.setRequestId(requestId);
        header.setSerializationType((byte) RpcSerializationType.JSON.ordinal());
        header.setMsgType((byte) MsgType.REQUEST.ordinal());
        header.setStatus((byte) MsgStatus.SUCCESS.ordinal());
        rpcProtocol.setHeader(header);
        // 构建消息体
        final RpcRequest rpcRequest = new RpcRequest();
        final Class<CalcService> objClass = CalcService.class;
        rpcRequest.setClassName(objClass.getName());
        final Method method = objClass.getMethod("calc1", Integer.class, Integer.class);
        rpcRequest.setMethodCode(method.hashCode());
        rpcRequest.setMethodName(method.getName());
        rpcRequest.setServiceVersion("1.0");
        rpcRequest.setParameterTypes(method.getParameterTypes());
        rpcRequest.setParameter(new Object[]{1, 2});
        rpcProtocol.setBody(rpcRequest);
        // 发送请求
        nettyClient.sendRequest(rpcProtocol);

        RpcFuture<RpcResponse> future = new RpcFuture(new DefaultPromise(new DefaultEventLoop()), 3000L);

        RpcRequestTracker.REQUEST_MAP.put(requestId, future);
        RpcResponse rpcResponse = future.getPromise().sync().get(future.getTimeout(), TimeUnit.MILLISECONDS);
        System.out.println(rpcResponse.getData());
    }
}
