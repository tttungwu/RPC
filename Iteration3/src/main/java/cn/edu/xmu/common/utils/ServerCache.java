package cn.edu.xmu.common.utils;

import cn.edu.xmu.filter.FilterChain;

import java.util.HashMap;
import java.util.Map;

public class ServerCache {
    public static Map<String,Object> SERVICE_MAP = new HashMap<>();

    public static FilterChain beforeFilterChain = new FilterChain();

    public static FilterChain afterFilterChain = new FilterChain();

    private ServerCache() {

    }
}
