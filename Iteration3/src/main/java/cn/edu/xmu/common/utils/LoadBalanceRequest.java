package cn.edu.xmu.common.utils;

import java.util.List;

public class LoadBalanceRequest {
    private List<Endpoint> endpoints;

    public LoadBalanceRequest(List<Endpoint> endpoints) {
        this.endpoints = endpoints;
    }

    public List<Endpoint> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(List<Endpoint> endpoints) {
        this.endpoints = endpoints;
    }
}
