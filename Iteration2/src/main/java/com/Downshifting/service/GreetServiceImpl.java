package com.Downshifting.service;

import com.Downshifting.common.annotation.RpcService;

@RpcService
public class GreetServiceImpl implements GreetService{
    @Override
    public Object greet(String name) {
        return "Have a good day, " + name + "!";
    }
}
