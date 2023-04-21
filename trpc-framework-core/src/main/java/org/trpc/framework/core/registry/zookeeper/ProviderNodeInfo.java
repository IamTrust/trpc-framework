package org.trpc.framework.core.registry.zookeeper;

/**
 * 封装 Zookeeper 中的一个节点信息
 *
 * @author Trust会长
 * @Date 2023/4/21
 */
public class ProviderNodeInfo {
    private String serviceName;
    private String address;

    public ProviderNodeInfo() {
    }

    public ProviderNodeInfo(String serviceName, String address) {
        this.serviceName = serviceName;
        this.address = address;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "ProviderNodeInfo{" +
                "serviceName='" + serviceName + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
