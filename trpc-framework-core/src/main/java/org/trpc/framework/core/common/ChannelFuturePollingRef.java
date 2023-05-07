package org.trpc.framework.core.common;

import java.util.concurrent.atomic.AtomicLong;

import static org.trpc.framework.core.common.cache.CommonClientCache.SERVICE_ROUTER_MAP;

/**
 * 轮询
 *
 * @author Trust会长
 * @Date 2023/5/6
 */
public class ChannelFuturePollingRef {

    private AtomicLong referenceTimes = new AtomicLong(0);

    public ChannelFutureWrapper getChannelFutureWrapper(String serviceName){
        ChannelFutureWrapper[] arr = SERVICE_ROUTER_MAP.get(serviceName);
        long i = referenceTimes.getAndIncrement();
        int index = (int) (i % arr.length);
        return arr[index];
    }

}
