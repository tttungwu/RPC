package cn.edu.xmu.register;


import cn.edu.xmu.register.event.AddRpcEventData;
import cn.edu.xmu.register.event.RemoveEventData;
import cn.edu.xmu.register.event.RpcEventData;
import cn.edu.xmu.register.event.RpcListerLoader;
import cn.edu.xmu.common.utils.ClientCache;
import cn.edu.xmu.common.utils.Endpoint;
import cn.edu.xmu.common.utils.EndpointService;
import cn.edu.xmu.common.utils.Service;
import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelFuture;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.util.ArrayList;
import java.util.List;

import static org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent.Type.*;

public class CuratorZookeeperRegistry implements RegistryService{

    // 连接失败等待重试时间
    private static final int BASE_SLEEP_TIME_MS = 1000;
    // 重试次数
    private static final int MAX_RETRIES = 3;
    // 根路径
    private static final String ROOT = "/rpc";
    // 服务者路径
    private static final String SERVER = "/server";

    private final CuratorFramework client;

    public CuratorZookeeperRegistry(String registerAddr) {
        client = CuratorFrameworkFactory.newClient(registerAddr, new ExponentialBackoffRetry(BASE_SLEEP_TIME_MS, MAX_RETRIES));
        client.start();
    }

    @Override
    public void register(EndpointService endpointService) throws Exception {
        // 检查并创建根路径（如果不存在）
        if (!existNode(ROOT)) {
            client.create().creatingParentContainersIfNeeded()
                    .withMode(CreateMode.PERSISTENT)
                    .forPath(ROOT, "".getBytes());
        }

        final String serverPath = getServerPath(endpointService);

        // 如果节点已经存在，则删除它
        if (existNode(serverPath)) {
            deleteNode(serverPath);
        }

        // 创建提供者数据的临时节点
        client.create().creatingParentContainersIfNeeded()
                .withMode(CreateMode.EPHEMERAL)
                .forPath(serverPath, JSON.toJSONString(endpointService).getBytes());
    }

    @Override
    public void unRegister(EndpointService endpointService) throws Exception {
        // 删除节点
        String path = getServerPath(endpointService);
        if (!deleteNode(path)) {
            throw new RuntimeException("Failed to delete node: " + path);
        }

        Endpoint endpoint = endpointService.getEndpoint();
        Service service = endpointService.getService();
        // 从服务 -> 端点映射中移除指定端点
        List<Endpoint> endpoints = ClientCache.SERVICE_ENDPOINTS_MAP.get(service);
        if (endpoints != null) {
            endpoints.remove(endpoint);
        }
        // 从端点 -> channel映射中移除并关闭对应的ChannelFuture
        ChannelFuture channelFuture = ClientCache.ENDPOINT_CHANNEL_MAP.remove(endpoint);
        if (channelFuture != null) {
            channelFuture.channel().close();
        }
    }

    @Override
    public List<Endpoint> discovery(Service service) throws Exception {
        List<Endpoint> endPoints = ClientCache.SERVICE_ENDPOINTS_MAP.get(service);
        if (null == endPoints || endPoints.isEmpty()){
            final List<String> children = client.getChildren().forPath(getServicePath(service));
            if (!children.isEmpty()) {
                endPoints = new ArrayList<>();
                for (String child : children) {
                    final String[] split = child.split(":");
                    if (split.length == 2) {
                        endPoints.add(new Endpoint(split[0], Integer.parseInt(split[1])));
                    }
                }
            }
        }
        return endPoints;
    }

    // 客户端订阅服务
    @Override
    public void subscribe(Service service) throws Exception {
        final String path = getServicePath(service);
        ClientCache.SUBSCRIBE_SERVICE_LIST.add(service);
        this.watchNodeDataChange(path);
    }

    // 客户端取消订阅服务
    @Override
    public void unSubscribe(Service service) {
        final String path = getServicePath(service);

        // 从订阅列表中移除服务
        ClientCache.SUBSCRIBE_SERVICE_LIST.remove(service);
        // 从服务 -> 端点映射中移除对应的端点列表
        List<Endpoint> endpoints = ClientCache.SERVICE_ENDPOINTS_MAP.remove(service);

        if (endpoints != null) {
            // 对于每个端点，移除对应的ChannelFuture并关闭
            for (Endpoint endpoint : endpoints) {
                ChannelFuture channelFuture = ClientCache.ENDPOINT_CHANNEL_MAP.remove(endpoint);
                if (channelFuture != null) {
                    channelFuture.channel().close();
                }
            }
        }
    }

    private String getServicePath(Service service) {
        return ROOT + SERVER + "/" + service.getServiceName() + "/" + service.getVersion();
    }

    private String getServerPath(EndpointService endpointService) {
        Service service = endpointService.getService();
        Endpoint endpoint = endpointService.getEndpoint();
        return ROOT + SERVER + "/" + service.getServiceName() + "/" + service.getVersion()+"/"
                + endpoint.getIp() + ":" + endpoint.getPort();
    }

    // 监听指定服务
    public void watchNodeDataChange(String path) throws Exception {
        PathChildrenCache cache = new PathChildrenCache(client, path, true);

        // 启动PathChildrenCache
        cache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);

        // 添加PathChildrenCacheListener监听器
        cache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                final PathChildrenCacheEvent.Type type = event.getType();
                RpcEventData eventData = null;
                if (type.equals(CHILD_REMOVED)){
                    String path = event.getData().getPath();
                    final EndpointService endpointService = parsePath(path);
                    eventData = new RemoveEventData(endpointService);
                } else if (type.equals(CHILD_ADDED)){
                    String path = event.getData().getPath();
                    byte[] bytes = client.getData().forPath(path);
                    Object o = JSON.parseObject(bytes, EndpointService.class);
                    eventData = new AddRpcEventData(o);
                }
                RpcListerLoader.sendEvent(eventData);
            }
        });
    }

    // 解析路径
    private EndpointService parsePath(String path) throws Exception {
        // 验证路径格式是否正确
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("path can't be null");
        }

        String[] pathSegments = path.split("/");
        if (pathSegments.length < 6) {
            throw new IllegalArgumentException("Invalid path format：" + path);
        }

        // 提取路径中的服务名称和版本信息
        String serviceName = pathSegments[3];
        String version = pathSegments[4];

        // 提取主机和端口信息
        String[] hostPort = pathSegments[5].split(":");
        if (hostPort.length != 2) {
            throw new IllegalArgumentException("Invalid host and port format：" + pathSegments[5]);
        }

        String host = hostPort[0];
        int port;
        try {
            port = Integer.parseInt(hostPort[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid port number: " + hostPort[1], e);
        }

        Endpoint endpoint = new Endpoint(host, port);
        Service service = new Service(serviceName, version);

        return new EndpointService(endpoint, service);
    }

    public boolean deleteNode(String path) {
        try {
            client.delete().forPath(path);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean existNode(String path) {
        try {
            Stat stat = client.checkExists().forPath(path);
            return stat != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
