package com.Downshifting.comms.client;

import com.Downshifting.Register.RegistryFactory;
import com.Downshifting.Register.RegistryService;
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
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Client {

    private final Bootstrap bootstrap;

    private final EventLoopGroup eventLoopGroup;

    private ChannelFuture channelFuture;

    public Client() throws InterruptedException {
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
        ClientCache.BOOT_STRAP = bootstrap;
    }

    public void connectServer() throws Exception {
        for (Service service : ClientCache.SUBSCRIBE_SERVICE_LIST){
            final RegistryService registryService = RegistryFactory.get(RegisterType.ZOOKEEPER);
            final List<Endpoint> endpoints = registryService.discovery(service);
            ClientCache.SERVICE_ENDPOINTS_MAP.put(service, endpoints);
            for (Endpoint endpoint : endpoints) {
                final ChannelFuture connect = bootstrap.connect(endpoint.getIp(), endpoint.getPort());
                ClientCache.ENDPOINT_CHANNEL_MAP.put(endpoint, connect);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        final Client client = new Client();
        final RegistryService registryService = RegistryFactory.get(RegisterType.ZOOKEEPER);
        final Service service = new Service(CalcService.class.getName(), "1.0");
        registryService.subscribe(service);
        client.connectServer();
        final Proxy iproxy = ProxyFactory.getProxy(RpcProxyType.CG_LIB);
        final CalcService proxy = iproxy.getProxy(CalcService.class);
        while (true) {
            System.out.println(proxy.calc2(3, 4));
            Thread.sleep(1000);
        }
    }
}