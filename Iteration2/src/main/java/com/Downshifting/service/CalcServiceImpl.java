package com.Downshifting.service;

public class CalcServiceImpl implements CalcService{
    @Override
    public Object calc1(Integer a, Integer b) {
        return a + b;
    }

    @Override
    public Object calc2(Integer a, Integer b) {
        return a * b;
    }
}