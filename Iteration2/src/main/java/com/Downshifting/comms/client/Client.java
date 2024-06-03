package com.Downshifting.comms.client;

import com.Downshifting.common.RPC.*;
import com.Downshifting.common.constants.*;
import com.Downshifting.common.utils.ClientCache;
import com.Downshifting.common.utils.Endpoint;
import com.Downshifting.common.utils.Service;
import com.Downshifting.proxy.Proxy;
import com.Downshifting.proxy.ProxyFactory;
import com.Downshifting.service.CalcService;
import com.Downshifting.service.CalcServiceImpl;
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
    }

    public void registerBean(String serviceName){
        ClientCache.services.put(new Service(serviceName, "1.0"), server);
        channelFuture = bootstrap.connect(server.getIp(), server.getPort());
        ClientCache.channelFutureMap.put(server, channelFuture);
    }

    public void sendRequest(Object request) {
        channelFuture.channel().writeAndFlush(request);
    }

    public static void main(String[] args) throws Exception {
        final Client client = new Client(new Endpoint("127.0.0.1", 8084));
        client.registerBean(CalcServiceImpl.class.getName());
        final Proxy iproxy = ProxyFactory.getProxy(RpcProxyType.CG_LIB);
        final CalcServiceImpl proxy = iproxy.getProxy(CalcServiceImpl.class);
        System.out.println(proxy.calc2(2, 3));
        System.out.println(proxy.calc1(2, 3));
    }
}