package cn.edu.xmu.common.RPC;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


public class RpcRequest implements Serializable {
    // 服务版本
    private String serviceVersion;
    // 调用的类的全限定名
    private String className;
    // 调用的方法名称
    private String methodName;
    // 方法的哈希值
    private Integer methodCode;
    // 请求参数及类型
    private Object[] parameters;
    private Class<?>[] parameterTypes;

    private Map<String, Object> serviceAttachments = new HashMap<>();
    private Map<String, Object> clientAttachments = new HashMap<>();

    public Map<String, Object> getServiceAttachments() {
        return serviceAttachments;
    }

    public void setServiceAttachments(Map<String, Object> serviceAttachments) {
        this.serviceAttachments = serviceAttachments;
    }

    public Map<String, Object> getClientAttachments() {
        return clientAttachments;
    }

    public void setClientAttachments(Map<String, Object> clientAttachments) {
        this.clientAttachments = clientAttachments;
    }

    public String getServiceVersion() {
        return serviceVersion;
    }

    public void setServiceVersion(String serviceVersion) {
        this.serviceVersion = serviceVersion;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Integer getMethodCode() {
        return methodCode;
    }

    public void setMethodCode(Integer methodCode) {
        this.methodCode = methodCode;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }
}
