package org.trpc.framework.core.common.event.listener;

import org.trpc.framework.core.common.ChannelFutureWrapper;
import org.trpc.framework.core.common.event.TRpcListener;
import org.trpc.framework.core.common.event.TRpcNodeChangeEvent;
import org.trpc.framework.core.registry.URL;
import org.trpc.framework.core.registry.zookeeper.ProviderNodeInfo;

import java.util.List;

import static org.trpc.framework.core.cache.CommonClientCache.CONNECT_MAP;
import static org.trpc.framework.core.cache.CommonClientCache.IROUTER;

/**
 * @author Trust会长
 * @Date 2023/5/6
 */
public class ProviderNodeDataChangeListener implements TRpcListener<TRpcNodeChangeEvent> {
    @Override
    public void callback(Object t) {
        ProviderNodeInfo providerNodeInfo = ((ProviderNodeInfo) t);
        List<ChannelFutureWrapper> channelFutureWrappers =  CONNECT_MAP.get(providerNodeInfo.getServiceName());
        for (ChannelFutureWrapper channelFutureWrapper : channelFutureWrappers) {
            String address = channelFutureWrapper.getHost()+":"+channelFutureWrapper.getPort();
            if(address.equals(providerNodeInfo.getAddress())){
                //修改权重
                channelFutureWrapper.setWeight(providerNodeInfo.getWeight());
                URL url = new URL();
                url.setServiceName(providerNodeInfo.getServiceName());
                //更新权重
                IROUTER.updateWeight(url);
                break;
            }
        }
    }
}
