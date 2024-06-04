package cn.edu.xmu.Register;

import cn.edu.xmu.common.utils.Endpoint;
import cn.edu.xmu.common.utils.EndpointService;
import cn.edu.xmu.common.utils.Service;

import java.util.List;

public interface RegistryService {
    void register(EndpointService endpointService) throws Exception;

    void unRegister(EndpointService endpointService) throws Exception;

    List<Endpoint> discovery(Service service) throws Exception;

    void subscribe(Service service) throws Exception;

    void unSubscribe(Service service);
}
