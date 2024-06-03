package com.Downshifting.common.utils;

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
}