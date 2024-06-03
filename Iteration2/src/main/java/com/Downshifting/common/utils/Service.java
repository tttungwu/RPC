package com.Downshifting.common.utils;

import java.util.Objects;

public class Service {
    // 服务的名称
    private String serviceName;
    // 服务的版本号
    private String version;

    public Service(String serviceName, String version) {
        this.serviceName = serviceName;
        this.version = version;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Service service = (Service) o;
        return Objects.equals(serviceName, service.serviceName) && Objects.equals(version, service.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceName, version);
    }

    @Override
    public String toString() {
        return "Service{" +
                "serviceName='" + serviceName + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
