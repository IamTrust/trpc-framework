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

    private Integer weight;

    private String registryTime;

    public ProviderNodeInfo() {
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

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public String getRegistryTime() {
        return registryTime;
    }

    public void setRegistryTime(String registryTime) {
        this.registryTime = registryTime;
    }

    @Override
    public String toString() {
        return "ProviderNodeInfo{" +
                "serviceName='" + serviceName + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
