package org.trpc.framework.core.registry;

import org.trpc.framework.core.registry.zookeeper.ProviderNodeInfo;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 封装一个服务
 *
 * @author Trust会长
 * @Date 2023/4/21
 */
public class URL {
    /**
     * 服务名
     */
    private String applicationName;
    /**
     * 注册中心中的节点名
     */
    private String serviceName;
    /**
     * 其他参数，可自由扩展
     */
    private Map<String, String> params;

    public void addParam(String k, String v) {
        if (params == null)
            params = new HashMap<>();
        params.put(k, v);
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    /**
     * 将URL转换为写入zk的provider节点下的一段字符串
     *
     * @param url
     * @return
     */
    public static String buildProviderUrlStr(URL url) {
        String host = url.getParams().get("host");
        String port = url.getParams().get("port");
        String group = url.getParams().get("group");
        return new String((url.getApplicationName() + ";" +
                url.getServiceName() + ";" +
                host + ":" +
                port + ";" +
                System.currentTimeMillis() + ";100;" +
                group).getBytes(), StandardCharsets.UTF_8);
    }

    /**
     * 将URL转换为写入zk的consumer节点下的一段字符串
     *
     * @param url
     * @return
     */
    public static String buildConsumerUrlStr(URL url) {
        String host = url.getParams().get("host");
        return new String((url.getApplicationName() + ";" + url.getServiceName() + ";" + host + ";" + System.currentTimeMillis()).getBytes(), StandardCharsets.UTF_8);
    }


    /**
     * 将某个节点下的信息转换为一个Provider节点对象
     *
     * @param providerNodeStr
     * @return
     */
    public static ProviderNodeInfo buildURLFromUrlStr(String providerNodeStr) {
        String[] items = providerNodeStr.split(";");
        ProviderNodeInfo providerNodeInfo = new ProviderNodeInfo();
        providerNodeInfo.setApplicationName(items[0]);
        providerNodeInfo.setServiceName(items[1]);
        providerNodeInfo.setAddress(items[2]);
        providerNodeInfo.setRegistryTime(items[3]);
        providerNodeInfo.setWeight(Integer.valueOf(items[4]));
        providerNodeInfo.setGroup(String.valueOf(items[5]));
        return providerNodeInfo;
    }
}
