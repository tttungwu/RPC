package cn.edu.xmu.common.utils;

public class LoadBalanceResponse {

    private EndpointService selectedEndpointService;

    public LoadBalanceResponse(EndpointService selectedEndpointService) {
        this.selectedEndpointService = selectedEndpointService;
    }

    public EndpointService getSelectedEndpointService() {
        return selectedEndpointService;
    }

    public void setSelectedEndpointService(EndpointService selectedEndpointService) {
        this.selectedEndpointService = selectedEndpointService;
    }
}
