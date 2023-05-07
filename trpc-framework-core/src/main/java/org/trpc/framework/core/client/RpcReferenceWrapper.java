package org.trpc.framework.core.client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 封装 RPC 远程调用的目标
 *
 * @author Trust会长
 * @Date 2023/5/7
 */
public class RpcReferenceWrapper<T> {
    /**
     * 目标实现的接口
     */
    private Class<T> aimClass;
    /**
     * 附加信息
     */
    private Map<String,Object> attachments = new ConcurrentHashMap<>();

    public Class<T> getAimClass() {
        return aimClass;
    }

    public void setAimClass(Class<T> aimClass) {
        this.aimClass = aimClass;
    }

    public boolean isAsync(){
        Boolean res = (Boolean) attachments.get("async");
        if (res == null) {
            return false;
        }
        return res;
    }

    public void setAsync(boolean async){
        this.attachments.put("async",async);
    }

    public String getUrl(){
        return String.valueOf(attachments.get("url"));
    }

    public void setUrl(String url){
        attachments.put("url",url);
    }

    public String getServiceToken(){
        return String.valueOf(attachments.get("serviceToken"));
    }

    public void setServiceToken(String serviceToken){
        attachments.put("serviceToken",serviceToken);
    }

    public String getGroup(){
        return String.valueOf(attachments.get("group"));
    }

    public void setGroup(String group){
        attachments.put("group",group);
    }

    public Map<String, Object> getAttachments() {
        return attachments;
    }

    public void setAttachments(Map<String, Object> attachments) {
        this.attachments = attachments;
    }
}
