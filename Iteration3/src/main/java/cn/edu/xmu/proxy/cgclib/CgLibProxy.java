package cn.edu.xmu.proxy.cgclib;

import cn.edu.xmu.common.RPC.*;
import cn.edu.xmu.common.constants.*;
import cn.edu.xmu.common.utils.*;
import cn.edu.xmu.comms.client.ClientHandler;
import cn.edu.xmu.filter.FilterData;
import cn.edu.xmu.filter.FilterResponse;
import cn.edu.xmu.register.RegistryFactory;
import cn.edu.xmu.common.annotation.RpcReference;
import cn.edu.xmu.tolerant.FaultContext;
import cn.edu.xmu.tolerant.FaultTolerantFactory;
import cn.edu.xmu.tolerant.FaultTolerantStrategy;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.concurrent.DefaultPromise;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.codehaus.jackson.map.ObjectMapper;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class CgLibProxy implements MethodInterceptor {

    private final String serviceName;

    private final String version;

    private ChannelFuture loadBalancerFuture;

    private final long time;

    private final TimeUnit timeUnit;

    private final Bootstrap bootstrap;

    private final EventLoopGroup eventLoopGroup;


    // 请求ID
    public final static AtomicLong LOAD_BALANCE_REQUEST_ID_GEN = new AtomicLong(0);
    // 绑定请求
    public static final ConcurrentMap<Long, CompletableFuture<String>> LOAD_BALANCE_REQUEST_MAP = new ConcurrentHashMap<>();

    public CgLibProxy(Class clazz){
        this.serviceName = clazz.getName();
        final RpcReference rpcService = (RpcReference) clazz.getAnnotation(RpcReference.class);
        version = rpcService.version();
        time = rpcService.time();
        timeUnit = rpcService.timeUnit();

        bootstrap = new Bootstrap();
        eventLoopGroup = new NioEventLoopGroup(1);
        bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                .addLast(new StringDecoder())
                                .addLast(new StringEncoder())
                                .addLast(new SimpleChannelInboundHandler<String>() {
                                    @Override
                                    protected void channelRead0(ChannelHandlerContext ctx, String msg) {
                                        JSONObject jsonObject = JSON.parseObject(msg);
                                        long requestId = jsonObject.getLong("requestId");
                                        CompletableFuture<String> future = LOAD_BALANCE_REQUEST_MAP.remove(requestId);
                                        if (future != null) {
                                            future.complete(msg);
                                        }
                                    }
                                });
                    }
                });

        try {
            loadBalancerFuture = this.bootstrap.connect("127.0.0.1", 8088).sync();
        } catch (Exception e) {
            loadBalancerFuture = null;
            System.err.println("Failed to connect to load balancer: " + e.getMessage());
        }
    }

    // 构建请求头
    private ProtoHeader buildProtoHeader() {
        ProtoHeader header = new ProtoHeader();
        long requestId = RpcRequestTracker.getRequestId();
        header.setMagic(ProtocolConstants.MAGIC);
        header.setVersion(ProtocolConstants.VERSION);
        header.setRequestId(requestId);
        header.setSerializationType((byte) RpcSerializationType.JSON.ordinal());
        header.setMsgType((byte) MsgType.REQUEST.ordinal());
        header.setStatus((byte) 0x1);
        return header;
    }

    // 构建请求体
    private RpcRequest buildRpcRequest(Method method, Object[] objects) {
        RpcRequest rpcRequest = new RpcRequest();

        rpcRequest.setClassName(method.getDeclaringClass().getName());
        rpcRequest.setMethodCode(method.hashCode());
        rpcRequest.setMethodName(method.getName());
        rpcRequest.setServiceVersion("1.0");

        if (objects != null && objects.length > 0) {
            Class<?>[] parameterTypes = new Class[objects.length];
            for (int i = 0; i < objects.length; i++) {
                parameterTypes[i] = objects[i].getClass();
            }
            rpcRequest.setParameterTypes(parameterTypes);
            rpcRequest.setParameters(objects);
        }

        return rpcRequest;
    }

    Endpoint findServer(List<Endpoint> endpoints) throws Exception {
        Endpoint selected = endpoints.get(0);
        if (loadBalancerFuture != null) {
            if (LOAD_BALANCE_REQUEST_ID_GEN.longValue() == Long.MAX_VALUE){
                LOAD_BALANCE_REQUEST_ID_GEN.set(0);
            }
            Long requestId = LOAD_BALANCE_REQUEST_ID_GEN.incrementAndGet();
            LoadBalanceRequest loadBalanceRequest = new LoadBalanceRequest(requestId, endpoints);
            loadBalanceRequest.setLoadBalanceType(LoadBalanceType.Round);
            String jsonRequest = JSON.toJSONString(loadBalanceRequest);
            CompletableFuture<String> future = new CompletableFuture<>();
            LOAD_BALANCE_REQUEST_MAP.put(requestId, future);
            loadBalancerFuture.channel().writeAndFlush(jsonRequest).sync();
            String jsonResponse = future.get();
            LoadBalanceResponse response = JSON.parseObject(jsonResponse, LoadBalanceResponse.class);
            selected = response.getSelectedEndpoint();
        }

        return selected;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        final RpcProtocol rpcProtocol = new RpcProtocol();
        // 构建请求头
        final ProtoHeader header = buildProtoHeader();
        rpcProtocol.setHeader(header);
        // 构建请求体
        final RpcRequest rpcRequest = buildRpcRequest(method, objects);
        rpcProtocol.setBody(rpcRequest);
        rpcRequest.getClientAttachments().put("token", "xmu-rpc");

        FilterResponse filterResponse = ClientCache.beforeFilterChain.doFilter(new FilterData<>(rpcProtocol));
        if (!filterResponse.getIsAccepted()) {
            throw filterResponse.getException();
        }

        final List<Endpoint> endpoints = RegistryFactory.get(RegisterType.ZOOKEEPER).discovery(new Service(serviceName, version));
        if (endpoints.isEmpty()){
            throw new Exception("No service is available");
        }

        final Endpoint endpoint = findServer(endpoints);
        final ChannelFuture channelFuture = ClientCache.ENDPOINT_CHANNEL_MAP.get(endpoint);

        // 通过Netty的channel发送RPC协议对象
        channelFuture.channel().writeAndFlush(rpcProtocol);
        // 跟踪RPC请求的响应
        RpcFuture<RpcResponse> future = new RpcFuture(new DefaultPromise(new DefaultEventLoop()), 3000L);
        RpcRequestTracker.REQUEST_MAP.put(header.getRequestId(), future);
        RpcResponse rpcResponse = future.getPromise().sync().get(future.getTimeout(), TimeUnit.MILLISECONDS);

        // 发生异常
        if (rpcResponse.getException() != null){
            rpcResponse.getException().printStackTrace();
            final FaultContext faultContext = new FaultContext(endpoint, endpoints, rpcProtocol, rpcProtocol.getHeader().getRequestId(), rpcResponse.getException());
            final FaultTolerantStrategy faultTolerantStrategy = FaultTolerantFactory.get(FaultTolerantType.Retry);
            return faultTolerantStrategy.handler(faultContext);
        }
        return rpcResponse.getData();
    }
}
