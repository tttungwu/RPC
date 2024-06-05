package cn.edu.xmu.comms.server;

import cn.edu.xmu.filter.server.ServerAuthenticationFilter;
import cn.edu.xmu.register.RegistryFactory;
import cn.edu.xmu.register.RegistryService;
import cn.edu.xmu.common.RPC.RpcDecoder;
import cn.edu.xmu.common.RPC.RpcEncoder;
import cn.edu.xmu.common.annotation.RpcService;
import cn.edu.xmu.common.constants.RegisterType;
import cn.edu.xmu.common.utils.Endpoint;
import cn.edu.xmu.common.utils.EndpointService;
import cn.edu.xmu.common.utils.ServerCache;
import cn.edu.xmu.common.utils.Service;
import cn.edu.xmu.service.CalcServiceImpl;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.CountDownLatch;

public class Server {

    private String host;

    private final int port;

    private ServerBootstrap bootstrap;

    public Server(int port) {
        this.port = port;
        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        host = inetAddress.getHostAddress();
    }

    public void run(CountDownLatch latch) throws Exception {
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

            ChannelFuture future = bootstrap.bind(port).sync();
            latch.countDown(); // 服务器启动成功，减少计数
            future.channel().closeFuture().sync();
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
        if (!(annotation.serviceInterface().equals(void.class))) {
            serviceName = annotation.serviceInterface().getName();
        }
        Service service = new Service(serviceName, annotation.version());
        endpointService.setService(service);
        final RegistryService registryService = RegistryFactory.get(RegisterType.ZOOKEEPER);
        registryService.register(endpointService);
        final String key = String.join("$", serviceName, annotation.version());
        ServerCache.SERVICE_MAP.put(key, clazz.newInstance());
    }

    public static void main(String[] args) throws Exception {
        final Server server = new Server(8084);
        CountDownLatch latch = new CountDownLatch(1);

        // 启动服务器线程
        new Thread(() -> {
            try {
                server.run(latch);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        // 等待服务器启动成功
        latch.await();

        // 服务器启动成功后注册Bean
        server.registerBean(CalcServiceImpl.class);
    }
}