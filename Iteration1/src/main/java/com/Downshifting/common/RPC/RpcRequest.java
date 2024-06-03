package com.Downshifting.common.RPC;

import java.io.Serializable;


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
    private Object parameter;
    private Class<?> parameterTypes;

    public void setMethodCode(Integer methodCode) {
        this.methodCode = methodCode;
    }

    public Integer getMethodCode() {
        return methodCode;
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

    public Object getParameter() {
        return parameter;
    }

    public void setParameter(Object parameter) {
        this.parameter = parameter;
    }

    public void setParameterTypes(Class<?> parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public Class<?> getParameterTypes() {
        return parameterTypes;
    }
}
