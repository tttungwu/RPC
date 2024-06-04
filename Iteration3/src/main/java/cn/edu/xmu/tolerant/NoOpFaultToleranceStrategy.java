package cn.edu.xmu.tolerant;

public class NoOpFaultToleranceStrategy implements FaultTolerantStrategy{
    @Override
    public Object handler(FaultContext faultContext) throws Exception {
        return faultContext.getException();
    }
}
