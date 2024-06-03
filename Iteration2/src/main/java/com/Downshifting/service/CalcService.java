package com.Downshifting.service;

import com.Downshifting.common.annotation.RpcReference;

@RpcReference
public interface CalcService {
    Object calc1(Integer a, Integer b);

    Object calc2(Integer a, Integer b);
}