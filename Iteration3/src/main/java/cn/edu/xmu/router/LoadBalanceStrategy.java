package cn.edu.xmu.router;

import cn.edu.xmu.common.utils.EndpointService;

import java.util.List;

public interface LoadBalanceStrategy {
    EndpointService select(List<EndpointService> endpointServices);
}
