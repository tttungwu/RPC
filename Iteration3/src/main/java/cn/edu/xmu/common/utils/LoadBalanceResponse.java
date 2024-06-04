package cn.edu.xmu.common.utils;

public class LoadBalanceResponse {

    private Endpoint selectedEndpoint;

    public Endpoint getSelectedEndpoint() {
        return selectedEndpoint;
    }

    public void setSelectedEndpoint(Endpoint selectedEndpoint) {
        this.selectedEndpoint = selectedEndpoint;
    }
}
