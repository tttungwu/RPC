package cn.edu.xmu.common.tolerant;

public class NoOpFaultToleranceStrategy implements FaultTolerantStrategy{
    @Override
    public Object handler(FaultContext faultContext) throws Exception {
        return faultContext.getException();
    }
}
