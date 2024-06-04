package cn.edu.xmu.Register.event;


public class AddRpcEventData implements RpcEventData {

    private Object data;

    public AddRpcEventData(Object data) {
        this.data = data;
    }

    @Override
    public void setData(Object o) {
        this.data = data;
    }

    @Override
    public Object getData() {
        return data;
    }
}
