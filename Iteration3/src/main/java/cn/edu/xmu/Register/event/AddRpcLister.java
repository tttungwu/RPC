package cn.edu.xmu.Register.event;

import cn.edu.xmu.common.utils.ClientCache;
import cn.edu.xmu.common.utils.Endpoint;
import cn.edu.xmu.common.utils.EndpointService;
import cn.edu.xmu.common.utils.Service;
import io.netty.channel.ChannelFuture;

import java.util.ArrayList;

public class AddRpcLister implements RpcLister<AddRpcEventData> {
    @Override
    public void exec(AddRpcEventData addRpcEventData) {
        final EndpointService endpointService = (EndpointService) addRpcEventData.getData();
        final Service service = endpointService.getService();
        final Endpoint endpoint = endpointService.getEndpoint();

        // 使用 computeIfAbsent 简化初始化逻辑
        ClientCache.SERVICE_ENDPOINTS_MAP.computeIfAbsent(service, k -> new ArrayList<>()).add(endpoint);

        // 同步连接，确保连接成功后再放入 map 中
        if (!ClientCache.ENDPOINT_CHANNEL_MAP.containsKey(endpoint)) {
            try {
                ChannelFuture channelFuture = ClientCache.BOOT_STRAP.connect(endpoint.getIp(), endpoint.getPort()).sync();
                ClientCache.ENDPOINT_CHANNEL_MAP.put(endpoint, channelFuture);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // 恢复中断状态
                throw new RuntimeException("Failed to connect to " + endpoint, e);
            }
        }
    }
}
