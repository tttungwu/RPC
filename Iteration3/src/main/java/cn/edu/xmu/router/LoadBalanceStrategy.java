package cn.edu.xmu.router;

import cn.edu.xmu.common.utils.Endpoint;

import java.util.List;

public interface LoadBalanceStrategy {
    Endpoint select(List<Endpoint> endpointServices);
}
