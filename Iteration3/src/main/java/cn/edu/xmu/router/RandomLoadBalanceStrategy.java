package cn.edu.xmu.router;


import cn.edu.xmu.common.utils.Endpoint;

import java.util.List;
import java.util.Random;

public class RandomLoadBalanceStrategy implements LoadBalanceStrategy{
    @Override
    public Endpoint select(List<Endpoint> endpoints) {
        if (endpoints == null || endpoints.isEmpty()) {
            return null;
        }
        Random random = new Random();
        int index = random.nextInt(endpoints.size());
        return endpoints.get(index);
    }
}
