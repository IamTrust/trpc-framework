package org.trpc.framework.core.router;

import org.trpc.framework.core.common.ChannelFutureWrapper;
import org.trpc.framework.core.registry.URL;

import java.util.List;

import static org.trpc.framework.core.common.cache.CommonClientCache.*;

/**
 * 轮询策略
 *
 * @author Trust会长
 * @Date 2023/5/6
 */
public class RotateRouterImpl implements IRouter {
    @Override
    public void refreshRouterArr(Selector selector) {
        List<ChannelFutureWrapper> channelFutureWrappers = CONNECT_MAP.get(selector.getProviderServiceName());
        ChannelFutureWrapper[] arr = new ChannelFutureWrapper[channelFutureWrappers.size()];
        for (int i=0;i<channelFutureWrappers.size();i++) {
            arr[i]=channelFutureWrappers.get(i);
        }
        SERVICE_ROUTER_MAP.put(selector.getProviderServiceName(),arr);
    }

    @Override
    public ChannelFutureWrapper select(Selector selector) {
        return CHANNEL_FUTURE_POLLING_REF.getChannelFutureWrapper(selector.getProviderServiceName());
    }

    @Override
    public void updateWeight(URL url) {

    }
}
