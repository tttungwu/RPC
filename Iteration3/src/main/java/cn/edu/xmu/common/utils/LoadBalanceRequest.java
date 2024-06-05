package cn.edu.xmu.common.utils;

import cn.edu.xmu.common.constants.LoadBalanceType;

import java.util.List;

public class LoadBalanceRequest {

    private Long requestId;

    private List<Endpoint> endpoints;

    private LoadBalanceType loadBalanceType = LoadBalanceType.Random;

    public LoadBalanceRequest(Long requestId, List<Endpoint> endpoints, LoadBalanceType loadBalanceType) {
        this.requestId = requestId;
        this.endpoints = endpoints;
        this.loadBalanceType = loadBalanceType;
    }

    public LoadBalanceRequest(Long requestId, List<Endpoint> endpoints) {
        this.requestId = requestId;
        this.endpoints = endpoints;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public List<Endpoint> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(List<Endpoint> endpoints) {
        this.endpoints = endpoints;
    }

    public LoadBalanceType getLoadBalanceType() {
        return loadBalanceType;
    }

    public void setLoadBalanceType(LoadBalanceType loadBalanceType) {
        this.loadBalanceType = loadBalanceType;
    }
}
