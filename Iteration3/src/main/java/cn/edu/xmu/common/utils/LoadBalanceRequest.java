package cn.edu.xmu.common.utils;

import cn.edu.xmu.common.constants.LoadBalanceType;

import java.util.List;

public class LoadBalanceRequest {
    private List<EndpointService> endpointServices;

    private LoadBalanceType loadBalanceType = LoadBalanceType.Random;

    public LoadBalanceRequest(List<EndpointService> endpointServices) {
        this.endpointServices = endpointServices;
    }

    public List<EndpointService> getEndpointServices() {
        return endpointServices;
    }

    public void setEndpointServices(List<EndpointService> endpointServices) {
        this.endpointServices = endpointServices;
    }

    public LoadBalanceType getLoadBalanceType() {
        return loadBalanceType;
    }

    public void setLoadBalanceType(LoadBalanceType loadBalanceType) {
        this.loadBalanceType = loadBalanceType;
    }
}
