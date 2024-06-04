package cn.edu.xmu.common.tolerant;

public interface FaultTolerantStrategy {
    Object handler(FaultContext faultContext) throws Exception;
}
