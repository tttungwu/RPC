package cn.edu.xmu.tolerant;

import cn.edu.xmu.common.RPC.RpcProtocol;
import cn.edu.xmu.common.utils.EndpointService;

import java.util.List;

public class FaultContext {

    private EndpointService curEndpointService;

    private List<EndpointService> endpointServices;

    private RpcProtocol rpcProtocol;

    private Long requestId;

    private Exception exception;

    public FaultContext(EndpointService curEndpointService, List<EndpointService> endpointServices, RpcProtocol rpcProtocol, Long requestId, Exception exception) {
        this.curEndpointService = curEndpointService;
        this.endpointServices = endpointServices;
        this.rpcProtocol = rpcProtocol;
        this.requestId = requestId;
        this.exception = exception;
    }

    public EndpointService getCurEndpointService() {
        return curEndpointService;
    }

    public void setCurEndpointService(EndpointService curEndpointService) {
        this.curEndpointService = curEndpointService;
    }

    public List<EndpointService> getEndpointServices() {
        return endpointServices;
    }

    public void setEndpointServices(List<EndpointService> endpointServices) {
        this.endpointServices = endpointServices;
    }

    public RpcProtocol getRpcProtocol() {
        return rpcProtocol;
    }

    public void setRpcProtocol(RpcProtocol rpcProtocol) {
        this.rpcProtocol = rpcProtocol;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }
}
