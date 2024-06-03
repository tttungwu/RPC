package com.Downshifting.common.utils;

import java.util.Objects;

public class Endpoint {

    // IP地址
    private String ip;
    // 端口
    private Integer port;

    public Endpoint(String ip, Integer port) {
        this.ip = ip;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Endpoint endpoint = (Endpoint) o;
        return Objects.equals(ip, endpoint.ip) && Objects.equals(port, endpoint.port);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip, port);
    }

    @Override
    public String toString() {
        return "Endpoint{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                '}';
    }
}