package com.Downshifting.common.utils;

import java.util.Objects;

public class EndpointService {
    // 服务运行所在的主机端点
    Endpoint endpoint;
    // 服务的具体信息
    Service service;

    public EndpointService() {
    }

    public EndpointService(Endpoint endpoint, Service service) {
        this.endpoint = endpoint;
        this.service = service;
    }

    public Endpoint getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(Endpoint endpoint) {
        this.endpoint = endpoint;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EndpointService that = (EndpointService) o;
        return Objects.equals(endpoint, that.endpoint) && Objects.equals(service, that.service);
    }

    @Override
    public int hashCode() {
        return Objects.hash(endpoint, service);
    }

    @Override
    public String toString() {
        return "EndpointService{" +
                "endpoint=" + endpoint +
                ", service=" + service +
                '}';
    }
}
