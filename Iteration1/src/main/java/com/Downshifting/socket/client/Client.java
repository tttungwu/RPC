package com.Downshifting.socket.client;


import com.Downshifting.common.RPC.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultPromise;
import com.Downshifting.service.HelloService;
import com.Downshifting.common.constants.MsgType;
import com.Downshifting.common.constants.ProtocolConstants;
import com.Downshifting.common.constants.RpcSerializationType;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;


public class Client {

    private final String host;

    private final Integer port;

    private final Bootstrap bootstrap;
    private final EventLoopGroup eventLoopGroup;

    private ChannelFuture channelFuture;

    public Client(String host, Integer port) throws InterruptedException {
        this.host = host;
        this.port = port;

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
        channelFuture = bootstrap.connect(host,port).sync();
    }
    public void sendRequest(Object o){
        channelFuture.channel().writeAndFlush(o);
    }


    public static void main(String[] args) throws Exception {
        final Client nettyClient = new Client("127.0.0.1",8083);
        final RpcProtocol rpcProtocol = new RpcProtocol();
        // 构建消息头
        ProtoHeader header = new ProtoHeader();
        long requestId = RpcRequestTracker.getRequestId();
        header.setMagic(ProtocolConstants.MAGIC);
        header.setVersion(ProtocolConstants.VERSION);
        header.setRequestId(requestId);


        header.setSerializationType((byte) RpcSerializationType.JSON.ordinal());
        header.setMsgType((byte) MsgType.REQUEST.ordinal());
        header.setStatus((byte) 0x1);
        rpcProtocol.setHeader(header);
        final RpcRequest rpcRequest = new RpcRequest();

        final Class<HelloService> aClass = HelloService.class;
        rpcRequest.setClassName(aClass.getName());
        final Method method = aClass.getMethod("hello", String.class);
        rpcRequest.setMethodCode(method.hashCode());
        rpcRequest.setMethodName(method.getName());
        rpcRequest.setServiceVersion("1.0");
        rpcRequest.setParameterTypes(method.getParameterTypes()[0]);
        rpcRequest.setParameter("xhy~");
        rpcProtocol.setBody(rpcRequest);

        nettyClient.sendRequest(rpcProtocol);

        RpcFuture<RpcResponse> future = new RpcFuture(new DefaultPromise(new DefaultEventLoop()), 3000L);

        RpcRequestTracker.REQUEST_MAP.put(requestId, future);
        Object rpcResponse = future.getPromise().sync().get(future.getTimeout(), TimeUnit.MILLISECONDS);
        System.out.println(rpcResponse);



    }
}
