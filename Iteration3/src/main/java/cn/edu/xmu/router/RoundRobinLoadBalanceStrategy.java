package cn.edu.xmu.router;

import cn.edu.xmu.common.utils.Endpoint;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinLoadBalanceStrategy implements LoadBalanceStrategy{

    private static AtomicInteger roundRobinId = new AtomicInteger(0);

    @Override
    public Endpoint select(List<Endpoint> endpoints) {
        if (roundRobinId.get() == Integer.MAX_VALUE){
            roundRobinId.set(0);
        }
        return endpoints.get(roundRobinId.getAndIncrement() % endpoints.size());
    }
}
