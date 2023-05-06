package org.trpc.framework.core.router;

public class Selector {
    /**
     * 服务名
     */
    private String providerServiceName;

    public String getProviderServiceName() {
        return providerServiceName;
    }

    public void setProviderServiceName(String providerServiceName) {
        this.providerServiceName = providerServiceName;
    }
}
