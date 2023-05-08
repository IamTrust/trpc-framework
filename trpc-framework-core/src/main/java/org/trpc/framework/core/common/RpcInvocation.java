package org.trpc.framework.core.common;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 封装一次远程调用
 *
 * @author Trust会长
 * @Date 2023/4/20
 */
public class RpcInvocation implements Serializable {
    // 请求的目标方法
    private String targetMethod;
    // 请求的目标服务名称
    private String targetServiceName;
    // 请求参数信息
    private Object[] args;
    // 唯一标识
    private String uuid;
    // 接口响应的数据塞入这个字段中（如果是异步调用或者void类型，这里就为空）
    private Object response;
    // 附加信息
    private Map<String, Object> attachments = new ConcurrentHashMap<>();
    // 异常信息(如果被调用方法抛出异常则会塞入这个字段返回给客户端)
    private Throwable e;

    public Map<String, Object> getAttachments() {
        return attachments;
    }

    public void setAttachments(Map<String, Object> attachments) {
        this.attachments = attachments;
    }

    public String getTargetMethod() {
        return targetMethod;
    }

    public void setTargetMethod(String targetMethod) {
        this.targetMethod = targetMethod;
    }

    public String getTargetServiceName() {
        return targetServiceName;
    }

    public void setTargetServiceName(String targetServiceName) {
        this.targetServiceName = targetServiceName;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
    }

    public Throwable getE() {
        return e;
    }

    public void setE(Throwable e) {
        this.e = e;
    }
}
