package cn.edu.xmu.Register.event;


public class RemoveEventData implements RpcEventData {

    private Object o;

    public RemoveEventData(Object o) {
        this.o = o;
    }

    @Override
    public void setData(Object o) {
        this.o = o;
    }

    @Override
    public Object getData() {
        return o;
    }
}
