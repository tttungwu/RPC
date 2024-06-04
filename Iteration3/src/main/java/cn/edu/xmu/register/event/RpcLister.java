package cn.edu.xmu.register.event;

public interface RpcLister<T> {
    void exec(T t);
}
