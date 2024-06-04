package cn.edu.xmu.router;

import cn.edu.xmu.common.utils.EndpointService;

import java.util.List;
import java.util.Random;

public class RandomLoadBalanceStrategy implements LoadBalanceStrategy{
    @Override
    public EndpointService select(List<EndpointService> endpointServices) {
        if (endpointServices == null || endpointServices.isEmpty()) {
            return null;
        }
        Random random = new Random();
        int index = random.nextInt(endpointServices.size());
        return endpointServices.get(index);
    }
}
