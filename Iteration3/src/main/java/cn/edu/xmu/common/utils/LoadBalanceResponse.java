package cn.edu.xmu.common.utils;

public class LoadBalanceResponse {

    private Long requestId;

    private Endpoint selectedEndpoint;

    public LoadBalanceResponse(Long requestId, Endpoint selectedEndpoint) {
        this.requestId = requestId;
        this.selectedEndpoint = selectedEndpoint;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public Endpoint getSelectedEndpoint() {
        return selectedEndpoint;
    }

    public void setSelectedEndpoint(Endpoint selectedEndpoint) {
        this.selectedEndpoint = selectedEndpoint;
    }
}
