package cn.edu.xmu.filter.server;

import cn.edu.xmu.common.RPC.RpcProtocol;
import cn.edu.xmu.common.RPC.RpcRequest;
import cn.edu.xmu.filter.Filter;
import cn.edu.xmu.filter.FilterData;
import cn.edu.xmu.filter.FilterResponse;

public class ServerAuthenticationFilter implements Filter {
    @Override
    public FilterResponse doFilter(FilterData filterData) {
        final RpcProtocol rpcProtocol = (RpcProtocol) filterData.getObject();
        final RpcRequest rpcRequest = (RpcRequest) rpcProtocol.getBody();
        Object value = rpcRequest.getClientAttachments().get("token");
        if (!value.equals("xmu_rpc")) {
            return new FilterResponse(new Exception("invalid token"));
        }
        return new FilterResponse(true);
    }
}
