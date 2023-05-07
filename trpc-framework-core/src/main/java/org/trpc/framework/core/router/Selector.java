package org.trpc.framework.core.router;

import org.trpc.framework.core.common.ChannelFutureWrapper;

public class Selector {
    /**
     * 服务名
     */
    private String providerServiceName;

    /**
     * 经过二次筛选后的 future 集合
     */
    private ChannelFutureWrapper[] channelFutureWrappers;

    public String getProviderServiceName() {
        return providerServiceName;
    }

    public void setProviderServiceName(String providerServiceName) {
        this.providerServiceName = providerServiceName;
    }

    public ChannelFutureWrapper[] getChannelFutureWrappers() {
        return channelFutureWrappers;
    }

    public void setChannelFutureWrappers(ChannelFutureWrapper[] channelFutureWrappers) {
        this.channelFutureWrappers = channelFutureWrappers;
    }
}
