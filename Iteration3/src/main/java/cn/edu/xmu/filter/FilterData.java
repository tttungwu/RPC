package cn.edu.xmu.filter;

public class FilterData<T> {

    private T object;

    public FilterData(T object) {
        this.object = object;
    }

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }
}
