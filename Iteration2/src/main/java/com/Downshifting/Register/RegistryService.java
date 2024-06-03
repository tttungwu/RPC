package com.Downshifting.Register;

import com.Downshifting.common.utils.Endpoint;
import com.Downshifting.common.utils.EndpointService;
import com.Downshifting.common.utils.Service;

import java.util.List;

public interface RegistryService {
    void register(EndpointService endpointService) throws Exception;

    void unRegister(EndpointService endpointService) throws Exception;

    List<Endpoint> discovery(Service service) throws Exception;

    void subscribe(Service service) throws Exception;

    void unSubscribe(Service service);
}
