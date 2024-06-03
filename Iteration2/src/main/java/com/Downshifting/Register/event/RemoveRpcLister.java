package com.Downshifting.Register.event;

import com.Downshifting.common.utils.ClientCache;
import com.Downshifting.common.utils.Endpoint;
import com.Downshifting.common.utils.EndpointService;
import com.Downshifting.common.utils.Service;
import io.netty.channel.ChannelFuture;

import java.util.ArrayList;

public class RemoveRpcLister implements RpcLister<RemoveEventData>{
    @Override
    public void exec(RemoveEventData removeEventData) {
        final EndpointService endpointService = (EndpointService) removeEventData.getData();
        final Service service = endpointService.getService();
        final Endpoint endpoint = endpointService.getEndpoint();

        // 获取服务URL列表并移除特定URL
        ClientCache.SERVICE_ENDPOINTS_MAP.getOrDefault(service, new ArrayList<>()).remove(endpoint);

        // 移除ChannelFuture并关闭连接
        ChannelFuture channelFuture = ClientCache.ENDPOINT_CHANNEL_MAP.remove(endpoint);
        if (channelFuture != null) {
            channelFuture.channel().close();
        }
    }
}
