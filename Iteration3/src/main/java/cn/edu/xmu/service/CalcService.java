package cn.edu.xmu.service;

import cn.edu.xmu.common.annotation.RpcReference;

@RpcReference
public interface CalcService {
    Object calc1(Integer a, Integer b);

    Object calc2(Integer a, Integer b);
}