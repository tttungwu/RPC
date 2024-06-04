package cn.edu.xmu.router;

import cn.edu.xmu.common.utils.EndpointService;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinLoadBalanceStrategy implements LoadBalanceStrategy{

    private static AtomicInteger roundRobinId = new AtomicInteger(0);

    @Override
    public EndpointService select(List<EndpointService> endpointServices) {
        roundRobinId.addAndGet(1);
        if (roundRobinId.get() == Integer.MAX_VALUE){
            roundRobinId.set(0);
        }
        return endpointServices.get(roundRobinId.get() % endpointServices.size());
    }
}
