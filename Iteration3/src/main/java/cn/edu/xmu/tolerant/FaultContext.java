package cn.edu.xmu.tolerant;

import cn.edu.xmu.common.RPC.RpcProtocol;
import cn.edu.xmu.common.utils.Endpoint;
import cn.edu.xmu.common.utils.EndpointService;

import java.util.List;

public class FaultContext {

    private Endpoint curEndpoint;

    private List<Endpoint> endpoints;

    private RpcProtocol rpcProtocol;

    private Long requestId;

    private Exception exception;

    public FaultContext(Endpoint curEndpoint, List<Endpoint> endpoints, RpcProtocol rpcProtocol, Long requestId, Exception exception) {
        this.curEndpoint = curEndpoint;
        this.endpoints = endpoints;
        this.rpcProtocol = rpcProtocol;
        this.requestId = requestId;
        this.exception = exception;
    }

    public Endpoint getCurEndpoint() {
        return curEndpoint;
    }

    public void setCurEndpoint(Endpoint curEndpoint) {
        this.curEndpoint = curEndpoint;
    }

    public List<Endpoint> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(List<Endpoint> endpoints) {
        this.endpoints = endpoints;
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
