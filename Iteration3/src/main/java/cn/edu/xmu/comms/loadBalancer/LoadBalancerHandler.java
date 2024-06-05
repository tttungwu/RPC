package cn.edu.xmu.comms.loadBalancer;

import cn.edu.xmu.common.constants.LoadBalanceType;
import cn.edu.xmu.common.utils.Endpoint;
import cn.edu.xmu.common.utils.LoadBalanceRequest;
import cn.edu.xmu.common.utils.LoadBalanceResponse;
import cn.edu.xmu.router.LoadBalanceFactory;
import cn.edu.xmu.router.LoadBalanceStrategy;
import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.List;
import java.util.Random;

public class LoadBalancerHandler extends ChannelInboundHandlerAdapter {
    private final Random random = new Random();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            String json = (String) msg;
            LoadBalanceRequest request = JSON.parseObject(json, LoadBalanceRequest.class);
            List<Endpoint> endpoints = request.getEndpoints();

            if (endpoints == null || endpoints.isEmpty()) {
                throw new IllegalArgumentException("Endpoints list cannot be null or empty");
            }

            LoadBalanceType loadBalanceType = request.getLoadBalanceType();
            LoadBalanceStrategy strategy = LoadBalanceFactory.get(loadBalanceType);

            Endpoint selected = strategy.select(endpoints);
            LoadBalanceResponse response = new LoadBalanceResponse(request.getRequestId(), selected);
            String jsonResponse = JSON.toJSONString(response);
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
