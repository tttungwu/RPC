package com.Downshifting.service;

import com.Downshifting.common.annotation.RpcService;

@RpcService
public class CalcServiceImpl implements CalcService{
    @Override
    public Object calc1(Integer a, Integer b) {
        Integer res = a + b;
        return "server1: " + res;
    }

    @Override
    public Object calc2(Integer a, Integer b) {
        Integer res = a * b;
        return "server1: " + res;
    }
}