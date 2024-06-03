package com.Downshifting.comms.server;

import com.Downshifting.Register.RegistryFactory;
import com.Downshifting.Register.RegistryService;
import com.Downshifting.common.RPC.RpcDecoder;
import com.Downshifting.common.RPC.RpcEncoder;
import com.Downshifting.common.annotation.RpcService;
import com.Downshifting.common.constants.RegisterType;
import com.Downshifting.common.utils.Endpoint;
import com.Downshifting.common.utils.EndpointService;
import com.Downshifting.common.utils.Service;
import com.Downshifting.service.CalcServiceImpl;
import com.Downshifting.service.CalcServiceImpl2;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Server2 {

    private String host;

    private final int port;

    private ServerBootstrap bootstrap;

    public Server2(int port) {
        this.port = port;
        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        host = inetAddress.getHostAddress();
    }

    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new RpcEncoder());
                            ch.pipeline().addLast(new RpcDecoder());
                            ch.pipeline().addLast(new ServerHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            bootstrap.bind(port).sync().channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public void registerBean(Class clazz) throws Exception {
        final EndpointService endpointService = new EndpointService();
        final Endpoint endpoint = new Endpoint(host, port);
        endpointService.setEndpoint(endpoint);
        if (!clazz.isAnnotationPresent(RpcService.class)) {
            throw new Exception(clazz.getName() + " is illegal");
        }
        final RpcService annotation = (RpcService) clazz.getAnnotation(RpcService.class);
        String serviceName = clazz.getInterfaces()[0].getName();
        if(!(annotation.serviceInterface().equals(void.class))){
            serviceName = annotation.serviceInterface().getName();
        }
        Service service = new Service(serviceName, annotation.version());
        endpointService.setService(service);
        final RegistryService registryService = RegistryFactory.get(RegisterType.ZOOKEEPER);
        registryService.register(endpointService);
    }

    public static void main(String[] args) throws Exception {
        final Server2 server = new Server2(8085);
        server.registerBean(CalcServiceImpl2.class);
        server.run();
    }
}