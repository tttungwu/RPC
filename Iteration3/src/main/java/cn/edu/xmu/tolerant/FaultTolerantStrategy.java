package cn.edu.xmu.tolerant;

public interface FaultTolerantStrategy {
    Object handler(FaultContext faultContext) throws Exception;
}
