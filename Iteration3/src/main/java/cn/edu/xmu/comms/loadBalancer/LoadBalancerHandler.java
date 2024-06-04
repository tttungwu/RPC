package cn.edu.xmu.comms.loadBalancer;

import cn.edu.xmu.common.constants.LoadBalanceType;
import cn.edu.xmu.common.utils.EndpointService;
import cn.edu.xmu.common.utils.LoadBalanceRequest;
import cn.edu.xmu.common.utils.LoadBalanceResponse;
import cn.edu.xmu.router.LoadBalanceFactory;
import cn.edu.xmu.router.LoadBalanceStrategy;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.codehaus.jackson.map.ObjectMapper;

import java.util.List;
import java.util.Random;

public class LoadBalancerHandler extends ChannelInboundHandlerAdapter {
    private final Random random = new Random();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            String json = (String) msg;
            ObjectMapper objectMapper = new ObjectMapper();
            LoadBalanceRequest request = objectMapper.readValue(json, LoadBalanceRequest.class);
            List<EndpointService> endpointServices = request.getEndpointServices();

            if (endpointServices == null || endpointServices.isEmpty()) {
                throw new IllegalArgumentException("Endpoints list cannot be null or empty");
            }

            LoadBalanceType loadBalanceType = request.getLoadBalanceType();
            LoadBalanceStrategy strategy = LoadBalanceFactory.get(loadBalanceType);

            EndpointService selected = strategy.select(endpointServices);
            LoadBalanceResponse response = new LoadBalanceResponse(selected);
            String jsonResponse = objectMapper.writeValueAsString(response);
            ctx.writeAndFlush(jsonResponse);
        } catch (Exception e) {
            e.printStackTrace();
            ctx.writeAndFlush("Error processing request: " + e.getMessage());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
